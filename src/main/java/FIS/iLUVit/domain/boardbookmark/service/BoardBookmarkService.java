package FIS.iLUVit.domain.boardbookmark.service;

import FIS.iLUVit.domain.blocked.domain.Blocked;
import FIS.iLUVit.domain.blocked.repository.BlockedRepository;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.exception.BoardErrorResult;
import FIS.iLUVit.domain.board.exception.BoardException;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.domain.Bookmark;
import FIS.iLUVit.domain.boardbookmark.exception.BoardBookmarkErrorResult;
import FIS.iLUVit.domain.boardbookmark.exception.BoardBookmarkException;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.post.repository.PostRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.board.dto.BoardBookmarkIdResponse;
import FIS.iLUVit.domain.board.dto.BoardStoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardBookmarkService {

    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final BlockedRepository blockedRepository;


    /**
     * 즐겨찾는 게시판 전체 조회
     */
    public List<BoardStoryResponse> findBoardBookmarkByUser(Long userId) {
        User user = getUser(userId);

        List<Long> blockedUserIds = blockedRepository.findByBlockingUser(user).stream()
                .map(Blocked::getBlockedUser)
                .map(User::getId)
                .collect(Collectors.toList());

        // 북마크한 게시판들
        List<Board> boards = boardBookmarkRepository.findByUser(user).stream()
                .map(Bookmark::getBoard)
                .collect(Collectors.toList());

        // 게시판들과 게시판들이 속해있는 센터를 매핑한다 ( 모두의 이야기 게시판이면 new Center() 넣어준다 )
        Map<Center, List<Board>> centerBoardMap = boards.stream()
                .collect(Collectors.groupingBy((board -> {
                    return board.getCenter()==null? new Center(): board.getCenter();
                })));

        List<BoardStoryResponse> boardStoryResponses = new ArrayList<>();

        /*
         * 시설별로 (시설-게시판리스트) 반복문 돈다
         */
        centerBoardMap.forEach((center, boardList) -> {

            List<BoardStoryResponse.BoardDto> boardDtoList = new ArrayList<>();

            /*
             * 게시판 별로 반복문 돈다
             */
            boardList.forEach(board -> {
                Long boardId = board.getId();
                String boardName = board.getName();
                String postTitle = null;
                Long postId = null;

                List<Post> posts = postRepository.findByBoardAndUserIdNotIn(board, blockedUserIds);

                if (!posts.isEmpty()) { // 게시판에 게시물이 하나도 없을수도 있으므로 검사해줘야한다
                    postTitle = posts.get(0).getTitle(); // 게시판의 가장 최근 게시물 하나
                    postId = posts.get(0).getId();
                }

                BoardStoryResponse.BoardDto boardDto = new BoardStoryResponse.BoardDto(boardId, boardName, postTitle, postId);
                boardDtoList.add(boardDto);
            });

            String storyName = center.getId() == null ? "모두의 이야기" : center.getName(); // 센터 아이디 널이면 모두, 아니면 시설 이야기

            BoardStoryResponse boardStoryResponse = new BoardStoryResponse(center.getId(), storyName, boardDtoList);

            boardStoryResponses.add(boardStoryResponse);
        });

        return boardStoryResponses;

    }


    /**
     * 즐겨찾는 게시판 등록
     */
    public BoardBookmarkIdResponse saveBoardBookmark(Long userId, Long boardId) {
        User findUser = getUser(userId);

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_FOUND));

        Bookmark bookmark = boardBookmarkRepository.save(new Bookmark(findBoard, findUser));

        return new BoardBookmarkIdResponse(bookmark.getId());
    }

    /**
     * 즐겨찾는 게시판 삭제
     */
    public void deleteBoardBookmark(Long userId, Long bookmarkId) {
        Bookmark findBookmark = boardBookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BoardBookmarkException(BoardBookmarkErrorResult.BOARD_BOOKMARK_NOT_FOUND));

        if (!findBookmark.getUser().getId().equals(userId)) {
            throw new BoardBookmarkException(BoardBookmarkErrorResult.FORBIDDEN_ACCESS);
        }

        boardBookmarkRepository.delete(findBookmark);
    }

    /**
     * 해당 시설과 연관된 게시판의 게시판 즐겨찾기를 삭제한다
     */
    public void deleteBoardBookmarkByCenter(Teacher teacher){
        if (teacher.getApproval() == Approval.ACCEPT) { // 교사의 승인 상태가 ACCEPT인지 확인
            // 교사의 소속 시설과 관련된 게시판 조회
            List<Board> boards = boardRepository.findByCenter(teacher.getCenter());
            // 교사와 관련된 게시판 즐겨찾기 삭제
            boardBookmarkRepository.deleteByUserAndBoardIn(teacher, boards);
        }
    }

    /**
     * 기본 게시판들을 게시판 즐겨찾기에 추가
     */
    public void saveDefaultBoardBookmark(Center center, User user){
        List<Board> defaultBoards = null;
        if(center == null){
            defaultBoards = boardRepository.findByCenterIsNullAndIsDefaultTrue();
        }
        else {
            defaultBoards = boardRepository.findByCenterAndIsDefaultTrue(center);
        }
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.of(defaultBoard, user);
            boardBookmarkRepository.save(bookmark);
        }
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        return user;
    }

}
