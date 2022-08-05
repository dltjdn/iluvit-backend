package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
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
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public BookmarkMainDTO search(Long userId) {
        BookmarkMainDTO dto = new BookmarkMainDTO();
        // stream groupingBy가 null 키 값을 허용하지 않아서 임시 값으로 생성한 센터
        Center tmp = new Center();
        Map<Center, List<Board>> centerBoardMap = bookmarkRepository.findByUserWithBoard(userId)
                .stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(b -> b.getCenter() == null ?
                        tmp : b.getCenter()));

        // 유저의 즐찾 게시판에서 최신 글 하나씩 뽑아옴.
        List<Post> posts = bookmarkRepository.findPostByBoard(userId);

        // 최신 글 리스트를 센터로 그루핑함.
        Map<Center, List<Post>> centerPostMap = posts.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getCenter() == null ?
                        tmp : p.getBoard().getCenter()));

        // ~의 이야기 DTO의 리스트
        List<BookmarkMainDTO.StoryDTO> storyDTOS = new ArrayList<>();

        // 센터(이야기)-게시글리스트 Map 루프 돌림.
        centerPostMap.forEach((c, pl) -> {
            BookmarkMainDTO.StoryDTO storyDTO = new BookmarkMainDTO.StoryDTO();
            // (~의 이야기안의 게시판 + 최신글 1개씩) DTO를 모아 리스트로 만듬.
            Map<Board, List<Post>> boardPostMap = pl.stream()
                    .collect(Collectors.groupingBy(post -> post.getBoard()));
            List<Board> boardList = centerBoardMap.get(c);
            if (boardList == null) {
                boardList = new ArrayList<>();
            }
            for (Board board : boardList) {
                if (!boardPostMap.containsKey(board)) {
                    boardPostMap.put(board, new ArrayList<>());
                }
            }
            List<BookmarkMainDTO.BoardDTO> boardDTOS = new ArrayList<>();
            boardPostMap.forEach((b, p) -> {
                String postTitle = null;
                Long postId = null;
                if (!p.isEmpty()) {
                    Post gp = p.get(0);
                    postTitle = gp.getTitle();
                    postId = gp.getId();
                }
                BookmarkMainDTO.BoardDTO boardDTO = new BookmarkMainDTO.BoardDTO(
                        b.getId(), b.getName(), postTitle, postId);
                boardDTOS.add(boardDTO);
            });

//            List<BookmarkMainDTO.BoardDTO> boardDTOS = pl.stream()
//                    .map(p -> new BookmarkMainDTO.BoardDTO(
//                            p.getBoard().getId(), p.getBoard().getName(), p.getTitle(), p.getId()))
//                    .collect(Collectors.toList());

            // ~의 이야기에 (게시판+최신글) DTO 리스트 넣어줌.
            List<BookmarkMainDTO.BoardDTO> boardDTOasc = boardDTOS.stream()
                    .sorted(Comparator.comparing(b -> b.getBoard_id()))
                    .collect(Collectors.toList());
            storyDTO.setBoardDTOList(boardDTOasc);
            // 센터 아이디 널이면 모두, 아니면 시설 이야기
            if (c.getId() == null) {
                storyDTO.setCenter_id(null);
                storyDTO.setStory_name("모두의 게시판");
                dto.getStories().add(storyDTO);
            } else {
                storyDTO.setCenter_id(c.getId());
                storyDTO.setStory_name(c.getName());
                storyDTOS.add(storyDTO);
            }
        });

        // 시설의 이야기 리스트는 아이디로 정렬 후
        List<BookmarkMainDTO.StoryDTO> newDTO = storyDTOS.stream()
                .sorted(Comparator.comparing(BookmarkMainDTO.StoryDTO::getCenter_id))
                .collect(Collectors.toList());

        // 최종 결과 dto에 넣어서 반환함.
        newDTO.forEach(s -> dto.getStories().add(s));

        return dto;
    }

    public BookmarkMainDTO searchByDefault() {
        BookmarkMainDTO dto = new BookmarkMainDTO();
        List<Board> defaultBoards = boardRepository.findDefaultByModu();

        BookmarkMainDTO.StoryDTO storyDTO = new BookmarkMainDTO.StoryDTO(null, "모두의 이야기");
        Map<Board, List<Post>> boardPostMap = boardRepository.findPostByDefault()
                .stream()
                .collect(Collectors.groupingBy(p -> p.getBoard()));
        for (Board board : defaultBoards) {
            if (!boardPostMap.containsKey(board)) {
                boardPostMap.put(board, new ArrayList<>());
            }
        }

        List<BookmarkMainDTO.BoardDTO> boardDTOS = new ArrayList<>();
        boardPostMap.forEach((b, p) -> {
            String postTitle = null;
            Long postId = null;
            if (!p.isEmpty()) {
                Post gp = p.get(0);
                postTitle = gp.getTitle();
                postId = gp.getId();
            }
            BookmarkMainDTO.BoardDTO boardDTO = new BookmarkMainDTO.BoardDTO(
                    b.getId(), b.getName(), postTitle, postId);
            boardDTOS.add(boardDTO);
        });

        storyDTO.setBoardDTOList(boardDTOS);
        dto.getStories().add(storyDTO);
        return dto;
    }

    public Long create(Long userId, Long boardId) {
        if (userId == null) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.USER_NOT_EXIST));
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.BOARD_NOT_EXIST));
        Bookmark bookmark = new Bookmark(findBoard, findUser);
        return bookmarkRepository.save(bookmark).getId();
    }

    public Long delete(Long userId, Long bookmarkId) {
        if (userId == null) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        Bookmark findBookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorResult.BOOKMARK_NOT_EXIST));
        if (!Objects.equals(findBookmark.getUser().getId(), userId)) {
            throw new BookmarkException(BookmarkErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        bookmarkRepository.delete(findBookmark);
        return bookmarkId;
   }
}
