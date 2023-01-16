package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.BookmarkErrorResult;
import FIS.iLUVit.exception.BookmarkException;
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
public class BoardBookmarkService {

    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public BookmarkMainDTO search(Long userId) {
        BookmarkMainDTO dto = new BookmarkMainDTO();
        // stream groupingBy가 null 키 값을 허용하지 않아서 임시 값으로 생성한 센터 -> tmp = 모두의 이야기 센터
        Center tmp = new Center();

        // bookmark에서 즐겨찾는 게시판을 가져온 후 센터와 매핑
        Map<Center, List<Board>> centerBoardMap = getCenterBoardMapByBookmark(userId, tmp);

        // 유저의 즐찾 게시판에서 최신 글 하나씩 뽑아옴.
        // 최신 글 리스트를 센터로 그루핑함.
        Map<Center, List<Post>> centerPostMap = getCenterPostMapByBookmark(userId, tmp);

        // 센터-게시글 맵의 키에서 북마크의 센터(센터-게시판 맵)가 없으면 빈 배열과 함께 넣어줌.
        centerBoardMap.keySet()
                .stream()
                .filter(center -> !centerPostMap.containsKey(center))
                .forEach(center -> centerPostMap.put(center, new ArrayList<>()));

        // ~의 이야기 DTO의 리스트
        List<BookmarkMainDTO.StoryDTO> storyDTOS = new ArrayList<>();

        // 센터(이야기)-게시글리스트 Map 루프 돌림.
        centerPostMap.forEach((c, pl) -> {
            BookmarkMainDTO.StoryDTO storyDTO = new BookmarkMainDTO.StoryDTO();
            // (~의 이야기안의 게시판 + 최신글 1개씩) DTO를 모아 리스트로 만듬.
            Map<Board, List<Post>> boardPostMap = pl.stream()
                    .collect(Collectors.groupingBy(post -> post.getBoard()));

            // 센터의 게시판들을 가져옴. 없는 경우 null 반환됨.
            List<Board> boardList = centerBoardMap.get(c);

            if (boardList == null) {
                boardList = new ArrayList<>();
            }
            // storyDTO에 게시판 - 최신글 1개 매핑시킨 리스트를 넣어줌.
            updateStoryDTO(boardList, boardPostMap, storyDTO);
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
        List<BookmarkMainDTO.StoryDTO> sortedStoryDTOS = storyDTOS.stream()
                .sorted(Comparator.comparing(BookmarkMainDTO.StoryDTO::getCenter_id))
                .collect(Collectors.toList());

        // 최종 결과 dto에 넣어서 반환함. center_id Null 은 stream 으로 정렬이 불가능..
//        sortedStoryDTOS.forEach(s -> dto.getStories().add(s));

        dto.getStories().addAll(sortedStoryDTOS);
        return dto;
    }

    private Map<Center, List<Post>> getCenterPostMapByBookmark(Long userId, Center tmp) {
        return boardBookmarkRepository.findPostByBoard(userId).stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getCenter() == null ?
                        tmp : p.getBoard().getCenter()));
    }

    private Map<Center, List<Board>> getCenterBoardMapByBookmark(Long userId, Center tmp) {
        return boardBookmarkRepository.findByUserWithBoardAndCenter(userId)
                .stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(b -> b.getCenter() == null ?
                        tmp : b.getCenter()));
    }

    private void updateStoryDTO(List<Board> boardList, Map<Board, List<Post>> boardPostMap, BookmarkMainDTO.StoryDTO storyDTO) {
        // 게시판이 없는 경우 == 게시글이 하나도 없는 경우 -> 빈 배열 넣어줌.
        for (Board board : boardList) {
            if (!boardPostMap.containsKey(board)) {
                boardPostMap.put(board, new ArrayList<>());
            }
        }

        List<BookmarkMainDTO.BoardDTO> boardDTOS = new ArrayList<>();

        // 게시판 DTO 생성 -> boardDTOS 에 추가
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

        // 게시판 아이디 오름차순 정렬
        List<BookmarkMainDTO.BoardDTO> boardDTOasc = boardDTOS.stream()
                .sorted(Comparator.comparing(b -> b.getBoard_id()))
                .collect(Collectors.toList());
        // ~의 이야기에 (게시판+최신글) DTO 리스트 넣어줌.
        storyDTO.setBoardDTOList(boardDTOasc);
    }

    public BookmarkMainDTO searchByDefault() {
        BookmarkMainDTO dto = new BookmarkMainDTO();
        List<Board> defaultBoards = boardRepository.findDefaultByModu();

        BookmarkMainDTO.StoryDTO storyDTO = new BookmarkMainDTO.StoryDTO(null, "모두의 이야기");
        Map<Board, List<Post>> boardPostMap = boardRepository.findPostByDefault()
                .stream()
                .collect(Collectors.groupingBy(p -> p.getBoard()));
        updateStoryDTO(defaultBoards, boardPostMap, storyDTO);
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
        return boardBookmarkRepository.save(bookmark).getId();
    }

    public Long delete(Long userId, Long bookmarkId) {
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
