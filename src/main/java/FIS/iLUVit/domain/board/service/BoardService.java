package FIS.iLUVit.domain.board.service;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.exception.BoardErrorResult;
import FIS.iLUVit.domain.board.exception.BoardException;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.domain.Bookmark;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.child.repository.ChildRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.board.dto.BoardFindAllResponse;
import FIS.iLUVit.domain.board.dto.BoardCreateRequest;
import FIS.iLUVit.domain.board.dto.BoardStoryPreviewResponse;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
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
    public BoardFindAllResponse findBoardByPublicList(Long userId) {

        List<Board> boards = boardRepository.findByCenterIsNull(); // 모두의 이야기 내 모든 게시판
        List<BoardFindAllResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardFindAllResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = getUser(userId);

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardFindAllResponse.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardFindAllResponse.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });
        BoardFindAllResponse boardFindAllResponse = new BoardFindAllResponse(null, "모두의 이야기", bookmarkList, boardList);

        return boardFindAllResponse;
    }

    /**
     * 시설 이야기 게시판 전체 조회
     */
    public BoardFindAllResponse findAllBoardByCenter(Long userId, Long centerId) {
        Center findCenter = getCenter(centerId);

        List<Board> boards = boardRepository.findByCenter(findCenter);  // 시설 이야기 모든 게시판
        List<BoardFindAllResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardFindAllResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        User user = getUser(userId);

        boards.forEach(board -> {
            Optional<Bookmark> bookmark =  boardBookmarkRepository.findByUserAndBoard(user, board);
            if (bookmark.isEmpty()) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                boardList.add(new BoardFindAllResponse.BoardBookmarkDto(board));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                bookmarkList.add(new BoardFindAllResponse.BoardBookmarkDto(board,bookmark.get().getId()));
            }
        });

        BoardFindAllResponse boardFindAllResponse = new BoardFindAllResponse(centerId, findCenter.getName(), bookmarkList, boardList);

        return boardFindAllResponse;
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
        User findUser = getUser(userId);
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
    public Long saveNewBoard(Long userId, Long centerId, BoardCreateRequest request) {

        // 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (centerId == null) {
            boardRepository.findByCenterIsNullAndName(request.getBoardName())
                    .ifPresent((b) -> {
                        throw new BoardException(BoardErrorResult.DUPLICATE_BOARD_NAME);
                    });

            Board board = Board.createBoard(request.getBoardName(), request.getBoardKind(), null, false);
            return boardRepository.save(board).getId();
        }

        // 센터가 존재하는 지 검사
        Center findCenter = getCenter(centerId);

        // 시설의 이야기에서 센터에 속하지 않은 회원은 게시판 생성 불가
        User findUser = getUser(userId);

        if (findUser.getAuth() == Auth.PARENT && findUser instanceof Parent) {
            Parent parent = (Parent) findUser;
            List<Child> childs = childRepository.findByParentAndCenter(parent, findCenter);
            if (childs.isEmpty()) {
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
        return boardRepository.save(board).getId();
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
            if (t.getAuth() != Auth.DIRECTOR || t.getCenter() == null
                    || !Objects.equals(t.getCenter().getId(), findBoard.getCenter().getId())) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        }
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        return centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }


}
