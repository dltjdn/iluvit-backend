package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.exception.BookmarkException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<Post> posts = bookmarkRepository.findPostByBoard(userId);
        Center tmp = new Center();

        Map<Center, List<Post>> centerPostMap = posts.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard().getCenter() == null ?
                        tmp : p.getBoard().getCenter()));

        List<BookmarkMainDTO.StoryDTO> storyDTOS = new ArrayList<>();
        centerPostMap.forEach((c, pl) -> {
            BookmarkMainDTO.StoryDTO storyDTO = new BookmarkMainDTO.StoryDTO();
            List<BookmarkMainDTO.BoardDTO> boardDTOS = pl.stream()
                    .map(p -> new BookmarkMainDTO.BoardDTO(
                            p.getBoard().getId(), p.getBoard().getName(), p.getTitle()))
                    .collect(Collectors.toList());
            storyDTO.setBoardDTOList(boardDTOS);
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

        List<BookmarkMainDTO.StoryDTO> newDTO = storyDTOS.stream()
                .sorted(Comparator.comparing(BookmarkMainDTO.StoryDTO::getCenter_id))
                .collect(Collectors.toList());

        newDTO.forEach(s -> dto.getStories().add(s));

        return dto;
    }

    public void create(Long userId, Long boardId) {
        int max = bookmarkRepository.findMaxOrder();
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException("존재하지 않는 게시판"));

        Bookmark bookmark = new Bookmark(max + 1, findBoard, findUser);
        bookmarkRepository.save(bookmark);
    }

    public void delete(Long userId, Long bookmarkId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Bookmark findBookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkException("존재하지 않는 북마크"));
        if (!Objects.equals(findBookmark.getUser().getId(), findUser.getId())) {
            throw new UserException("삭제 권한 없는 유저");
        }
        bookmarkRepository.deleteById(bookmarkId);

   }
}
