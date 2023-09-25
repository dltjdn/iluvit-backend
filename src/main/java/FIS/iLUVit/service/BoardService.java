package FIS.iLUVit.service;

import FIS.iLUVit.dto.board.BoardIdResponse;
import FIS.iLUVit.dto.board.BoardListResponse;
import FIS.iLUVit.dto.board.BoardCreateRequest;
import FIS.iLUVit.dto.board.BoardStoryPreviewResponse;
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
    public BoardListResponse findBoardByPublicList(Long userId) {

        List<Board> boards = boardRepository.findByCenterIsNull(); // 모두의 이야기 내 모든 게시판
        List<BoardListResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardListResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardListResponse.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardListResponse.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });
        BoardListResponse boardListResponse = new BoardListResponse(null, "모두의 이야기", bookmarkList, boardList);

        return boardListResponse;
    }

    /**
     * 시설 이야기 게시판 전체 조회
     */
    public BoardListResponse findAllBoardByCenter(Long userId, Long centerId) {
        Center findCenter = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));


        List<Board> boards = boardRepository.findByCenter(findCenter);  // 시설 이야기 모든 게시판
        List<BoardListResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardListResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardListResponse.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardListResponse.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });

        BoardListResponse boardListResponse = new BoardListResponse(centerId, findCenter.getName(), bookmarkList, boardList);

        return boardListResponse;
    }

    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    public List<BoardStoryPreviewResponse> findStoryPreviewList(Long userId) {
        List<BoardStoryPreviewResponse> result = new ArrayList<>();
        result.add(new BoardStoryPreviewResponse(null));
        if (userId == null) {
            return result;
        }
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        if (findUser.getAuth() == Auth.PARENT && findUser instanceof Parent ) {
            Parent parent = (Parent) findUser;
            List<Child> children = childRepository.findByParent(parent);
            List<BoardStoryPreviewResponse> boardStoryPreviewResponseList = children.stream()
                    .filter(child -> child.getCenter() != null && child.getApproval() == Approval.ACCEPT)
                    .map(child -> new BoardStoryPreviewResponse(child.getCenter()))
                    .collect(Collectors.toList());
            result.addAll(boardStoryPreviewResponseList);
        } else if (findUser instanceof Teacher)  {
            Teacher teacher = (Teacher) findUser;
            Center findCenter = teacher.getCenter();
            Approval approval = teacher.getApproval();
            if (findCenter != null && approval == Approval.ACCEPT) {
                BoardStoryPreviewResponse boardStoryPreviewResponse = new BoardStoryPreviewResponse(findCenter);
                result.add(boardStoryPreviewResponse);
            }
        }
        return result;
    }

    /**
     * 게시판 생성
     */
    public BoardIdResponse saveNewBoard(Long userId, Long centerId, BoardCreateRequest request) {

        // 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (centerId == null) {
            boardRepository.findByCenterIsNullAndName(request.getBoardName())
                    .ifPresent((b) -> {
                        throw new BoardException(BoardErrorResult.DUPLICATE_BOARD_NAME);
                    });
            Long boardId = boardRepository.save(Board.createBoard(
                    request.getBoardName(), request.getBoardKind(), null, false)).getId();
            return new BoardIdResponse(boardId);
        }

        // 센터가 존재하는 지 검사
        Center findCenter = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));

        // 시설의 이야기에서 센터에 속하지 않은 회원은 게시판 생성 불가
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        if (findUser.getAuth() == Auth.PARENT && findUser instanceof Parent) {
            Parent parent = (Parent) findUser;
            boolean childless = childRepository.findByParentAndCenter(parent, findCenter)
                    .isEmpty();
            if (childless) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        } else if (findUser instanceof Teacher) {
            Teacher teacher = (Teacher) findUser;
            if (teacher.getCenter() == null || !Objects.equals(teacher.getCenter().getId(), centerId)) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        }

        // 시설의 이야기에서 게시판 이름 중복성 검사 및 저장
        boardRepository.findByCenterAndName(findCenter,request.getBoardName())
                .ifPresent((b) -> {
                    throw new BoardException(BoardErrorResult.DUPLICATE_BOARD_NAME);
                });


        Board board = Board.createBoard(request.getBoardName(), request.getBoardKind(), findCenter,false);
        Board savedBoard = boardRepository.save(board);
        return new BoardIdResponse(savedBoard.getId());
    }

    /**
     * 게시판 삭제
     */
    public void deleteBoardWithValidation(Long userId, Long boardId) {

        // board id 오류
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_FOUND));

        /*
         * 1. 학부모 -> 삭제 불가
         * 2. DIRECTOR 권한X -> 삭제 불가
         * 3. 센터 값X -> 삭제 불가
         * 4. 원장이 속한 센터 != 게시판이 속한 센터 -> 삭제 불가
         * 5. 디폴트 게시판 -> 삭제 불가
         */

        if (findBoard.getIsDefault()) {
            throw new BoardException(BoardErrorResult.CANNOT_DELETE_DEFAULT_BOARD);
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
            throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
        } else {
            Teacher t = (Teacher) u;
            if (t.getAuth() != Auth.DIRECTOR) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
            if (t.getCenter() == null) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
            if (t.getCenter().getId() != findBoard.getCenter().getId()) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        }
    }
  
}
