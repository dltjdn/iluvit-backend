package FIS.iLUVit.service;

import FIS.iLUVit.dto.board.BoardIdDto;
import FIS.iLUVit.dto.board.BoardListDto;
import FIS.iLUVit.dto.board.BoardRequest;
import FIS.iLUVit.dto.board.StoryPreviewDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public BoardListDto findBoardByPublicList(Long userId) {

        List<Board> boards = boardRepository.findByCenterIsNull(); // 모두의 이야기 내 모든 게시판
        List<BoardListDto.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardListDto.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardListDto.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardListDto.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });
        BoardListDto boardListDto = new BoardListDto(null, "모두의 이야기", bookmarkList, boardList);

        return boardListDto;
    }

    /**
     * 시설 이야기 게시판 전체 조회
     */
    public BoardListDto findAllBoardByCenter(Long userId, Long centerId) {
        Center findCenter = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));


        List<Board> boards = boardRepository.findByCenter(findCenter);  // 시설 이야기 모든 게시판
        List<BoardListDto.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardListDto.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardListDto.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardListDto.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });

        BoardListDto boardListDto = new BoardListDto(centerId, findCenter.getName(), bookmarkList, boardList);

        return boardListDto;
    }

    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    public List<StoryPreviewDto> findStoryPreviewList(Long userId) {
        List<StoryPreviewDto> result = new ArrayList<>();
        result.add(new StoryPreviewDto(null));
        if (userId == null) {
            return result;
        }
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        if (findUser.getAuth() == Auth.PARENT) {
            List<Child> children = userRepository.findChildrenWithCenter(userId);
            List<StoryPreviewDto> storyPreviewDtoList = children.stream()
                    .filter(child -> child.getCenter() != null && child.getApproval() == Approval.ACCEPT)
                    .map(child -> new StoryPreviewDto(child.getCenter()))
                    .collect(Collectors.toList());
            result.addAll(storyPreviewDtoList);
        } else {
            Center findCenter = ((Teacher) findUser).getCenter();
            Approval approval = ((Teacher) findUser).getApproval();
            if (findCenter != null && approval == Approval.ACCEPT) {
                StoryPreviewDto storyPreviewDto = new StoryPreviewDto(findCenter);
                result.add(storyPreviewDto);
            }
        }
        return result;
    }

    /**
     * 게시판 생성
     */
    public BoardIdDto saveNewBoard(Long userId, Long center_id, BoardRequest request) {
        // userId 가 null 인 경우 게시판 생성 제한
        if (userId == null) {
            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (center_id == null) {
            boardRepository.findByCenterIsNullAndName(request.getBoardName())
                    .ifPresent((b) -> {
                        throw new BoardException(BoardErrorResult.BOARD_NAME_DUPLICATION);
                    });
            Long boardId = boardRepository.save(Board.createBoard(
                    request.getBoardName(), request.getBoardKind(), null, false)).getId();
            return new BoardIdDto(boardId);
        }

        // 센터가 존재하는 지 검사
        Center findCenter = centerRepository.findById(center_id)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 시설의 이야기에서 센터에 속하지 않은 회원은 게시판 생성 불가
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("유저 아이디 오류"));

        if (findUser.getAuth() == Auth.PARENT) {
            boolean childless = childRepository.findByParentAndCenter(userId, center_id)
                    .isEmpty();
            if (childless) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        } else {
            Teacher teacher = (Teacher) findUser;
            if (teacher.getCenter() == null || !Objects.equals(teacher.getCenter().getId(), center_id)) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }

        // 시설의 이야기에서 게시판 이름 중복성 검사 및 저장
        boardRepository.findByCenterAndName(findCenter,request.getBoardName())
                .ifPresent((b) -> {
                    throw new BoardException(BoardErrorResult.BOARD_NAME_DUPLICATION);
                });


        Board board = Board.createBoard(request.getBoardName(), request.getBoardKind(), findCenter,false);
        Board savedBoard = boardRepository.save(board);
        return new BoardIdDto(savedBoard.getId());
    }

    /**
     * 게시판 삭제
     */
    public void deleteBoardWithValidation(Long userId, Long boardId) {
        // userId 가 null 인 경우 게시판 삭제 제한
        if (userId == null) {
            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // board id 오류
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        /*
         * 1. 학부모 -> 삭제 불가
         * 2. DIRECTOR 권한X -> 삭제 불가
         * 3. 센터 값X -> 삭제 불가
         * 4. 원장이 속한 센터 != 게시판이 속한 센터 -> 삭제 불가
         * 5. 디폴트 게시판 -> 삭제 불가
         */

        if (findBoard.getIsDefault()) {
            throw new BoardException(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
        }
        userRepository.findById(userId)
                .ifPresent(u -> validateAuth(findBoard, u));

        boardRepository.delete(findBoard);
    }

    /**
     * 게시판 삭제를 위한 권한 조회
     */
    private void validateAuth(Board findBoard, User u) {
        if (u.getAuth() == Auth.PARENT) {
            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
        } else {
            Teacher t = (Teacher) u;
            if (t.getAuth() != Auth.DIRECTOR) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
            if (t.getCenter() == null) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
            if (t.getCenter().getId() != findBoard.getCenter().getId()) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }
    }
}
