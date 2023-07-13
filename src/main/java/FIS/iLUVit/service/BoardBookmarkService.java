package FIS.iLUVit.service;

import FIS.iLUVit.dto.board.BoardBookmarkIdDto;
import FIS.iLUVit.dto.board.StoryDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardBookmarkService {

    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final CenterRepository centerRepository;

    /**
     * 즐겨찾는 게시판 전체 조회
     */
    public List<StoryDto> findBoardBookmarkByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 유저가 즐겨찾기한 게시판별로 가장 최신 게시물들을 가져온다
        List<Post> posts =boardBookmarkRepository.findByUser(user).stream()
                .map(Bookmark::getBoard)
                .map(board -> postRepository.findByBoard(board, Sort.by(Sort.Direction.DESC, "id")).get(0))
                .collect(Collectors.toList());

        Map<Center, List<Post>> centerPostMap = posts.stream()
                .collect(Collectors.groupingBy(p -> {
                    Center center = p.getBoard().getCenter();
                    return center == null ? new Center() : center;
                }));

        List<StoryDto> storyDtos = new ArrayList<>();

        centerPostMap.forEach((center, postList) -> {

            List<StoryDto.BoardDto> boardDtoList =  new ArrayList<>();

            // post별 board
            postList.forEach((post)->{
                StoryDto.BoardDto boardDto = new StoryDto.BoardDto(post.getBoard().getId(), post.getBoard().getName(), post.getTitle(), post.getId());
                boardDtoList.add(boardDto);
            });

            String storyName = center.getId() == null ? "모두의 이야기" : center.getName(); // 센터 아이디 널이면 모두, 아니면 시설 이야기

            StoryDto storyDto = new StoryDto(center.getId(), storyName, boardDtoList);

            storyDtos.add(storyDto);

        });
        return storyDtos;
    }

    /**
     * 즐겨찾는 게시판 등록
     */
    public BoardBookmarkIdDto saveBoardBookmark(Long userId, Long boardId) {
        if (userId == null) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.USER_NOT_EXIST));
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.BOARD_NOT_EXIST));
        Bookmark bookmark = new Bookmark(findBoard, findUser);
        Long boardBookmarkId = boardBookmarkRepository.save(bookmark).getId();

        return new BoardBookmarkIdDto(boardBookmarkId);
    }

    /**
     * 즐겨찾는 게시판 삭제
     */
    public Long deleteBoardBookmark(Long userId, Long bookmarkId) {
        if (userId == null) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        Bookmark findBookmark = boardBookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.BOOKMARK_NOT_EXIST));
        if (!Objects.equals(findBookmark.getUser().getId(), userId)) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        boardBookmarkRepository.delete(findBookmark);
        return bookmarkId;
    }


}
