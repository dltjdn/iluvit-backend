package FIS.iLUVit.domain.board.service;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.exception.BoardErrorResult;
import FIS.iLUVit.domain.board.exception.BoardException;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.child.repository.ChildRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.board.dto.BoardFindAllResponse;
import FIS.iLUVit.domain.board.dto.BoardCreateRequest;
import FIS.iLUVit.domain.board.dto.BoardFindStoryResponse;
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
    private final TeacherRepository teacherRepository;

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public BoardFindAllResponse findBoardByPublicList(Long userId) {
        List<Board> boards = boardRepository.findByCenterIsNull(); // 모두의 이야기 내 모든 게시판

        List<BoardFindAllResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardFindAllResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        addBoardAndBookmarkList(userId, boards, bookmarkList, boardList);

        return BoardFindAllResponse.of(bookmarkList, boardList);
    }


    /**
     * 시설 이야기 게시판 전체 조회
     */
    public BoardFindAllResponse findAllBoardByCenter(Long userId, Long centerId) {
        Center center = getCenter(centerId);
        List<Board> boards = boardRepository.findByCenter(center);  // 시설 이야기 모든 게시판

        List<BoardFindAllResponse.BoardBookmarkDto> bookmarkList = new ArrayList<>();
        List<BoardFindAllResponse.BoardBookmarkDto> boardList = new ArrayList<>();
        addBoardAndBookmarkList(userId, boards, bookmarkList, boardList);

        return BoardFindAllResponse.of(centerId, center.getName(), bookmarkList, boardList);
    }


    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    public List<BoardFindStoryResponse> findStoryPreviewList(Long userId) {
        List<BoardFindStoryResponse> responses = new ArrayList<>();
        responses.add(BoardFindStoryResponse.from(null)); // 모두의 이야기 추가

        User user = getUser(userId);

        if (user.getAuth() == Auth.PARENT && user instanceof Parent ) { // 학부모일 때 시설 이야기 추가
            Parent parent = (Parent) user;
            List<Child> childs = childRepository.findByParent(parent);

            List<BoardFindStoryResponse> boardFindStoryResponseList = childs.stream()
                    .filter(child -> child.getCenter() != null && child.getApproval() == Approval.ACCEPT)
                    .map(Child::getCenter)
                    .map(BoardFindStoryResponse::from)
                    .collect(Collectors.toList());

            responses.addAll(boardFindStoryResponseList);

        } else if (user instanceof Teacher)  { // 선생님 일 때 시설 이야기 추가
            teacherRepository.findByIdAndApproval(user.getId(), Approval.ACCEPT)
                    .ifPresent((teacher) -> {
                        responses.add(BoardFindStoryResponse.from(teacher.getCenter()));
                    });
        }

        return responses;
    }

    /**
     * 게시판 생성
     */
    public Long saveNewBoard(Long userId, Long centerId, BoardCreateRequest request) {

        // 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (centerId == null) {
            boardRepository.findByCenterIsNullAndName(request.getBoardName())
                    .ifPresent((board) -> {
                        throw new BoardException(BoardErrorResult.DUPLICATE_BOARD_NAME);
                    });

            Board board = Board.publicOf(request.getBoardName(), request.getBoardKind(), false);
            return boardRepository.save(board).getId();
        }

        // 센터가 존재하는 지 검사
        Center center = getCenter(centerId);

        // 시설 이야기에서 시설에 속하지 않은 회원은 게시판 생성 불가 검증
        User user = getUser(userId);
        if (user.getAuth() == Auth.PARENT && user instanceof Parent) { // 학부모일 때 게시판 생성 권한 검증
            List<Child> childs = childRepository.findByParentAndCenter((Parent) user, center);

            if (childs.isEmpty()) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        } else if (user instanceof Teacher) { // 선생일 때 게시판 생성 권한 검증
            Teacher teacher = getTeacher(user.getId());

            if (!Objects.equals(teacher.getCenter().getId(), centerId)) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        }

        // 시설의 이야기에서 게시판 이름 중복성 검사 및 저장
        boardRepository.findByCenterAndName(center,request.getBoardName())
                .ifPresent((b) -> {
                    throw new BoardException(BoardErrorResult.DUPLICATE_BOARD_NAME);
                });

        Board board = Board.centerOf(request.getBoardName(), request.getBoardKind(), center,false);
        return boardRepository.save(board).getId();
    }

    /**
     * 게시판 삭제
     */
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_FOUND));

        // 디폴트 게시판은 삭제 불가
        if (board.getIsDefault()) {
            throw new BoardException(BoardErrorResult.CANNOT_DELETE_DEFAULT_BOARD);
        }

        // 유저의 게시판 삭제 권한 검증
        userRepository.findById(userId)
                .ifPresent(user -> validateDeleteBoardAuth(board, user));

        boardRepository.delete(board);
    }

    /**
     * 게시판 삭제 권한 검증
     */
    private void validateDeleteBoardAuth(Board board, User user) {
        if (user.getAuth() == Auth.PARENT) { // 학부모는 게시판 삭제 불가
            throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
        } else {
            Teacher teacher = getTeacher(user.getId());
            // 원장이 아니거나 게시판이 속한 센터에 속해있지 않으면 게시판 삭제 불가
            if (teacher.getAuth() != Auth.DIRECTOR || !Objects.equals(teacher.getCenter().getId(), board.getCenter().getId())) {
                throw new BoardException(BoardErrorResult.FORBIDDEN_ACCESS);
            }
        }
    }

    /**
     * 즐겨찾기 한 게시판과 즐겨찾기 게시판 하지 않은 게시판 리스트 만든다
     */
    private void addBoardAndBookmarkList(Long userId, List<Board> boards, List<BoardFindAllResponse.BoardBookmarkDto> bookmarkList, List<BoardFindAllResponse.BoardBookmarkDto> boardList) {
        User user = getUser(userId);
        boards.forEach(board -> {
            boardBookmarkRepository.findByUserAndBoard(user, board)
                    .ifPresentOrElse((boardBookmark)-> {
                        bookmarkList.add(BoardFindAllResponse.BoardBookmarkDto.of(board, boardBookmark.getId())); // 즐찾한 게시판들은 북마크 리스트에 넣음
                    }, () -> {
                        boardList.add(BoardFindAllResponse.BoardBookmarkDto.from(board)); // 즐찾 안한 게시판들은 보드 리스트에 넣음
                    });
        });
    }

    /**
     * 예외처리 - 존재하는 선생인가
     */
    private Teacher getTeacher(Long userId) {
        return teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
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
