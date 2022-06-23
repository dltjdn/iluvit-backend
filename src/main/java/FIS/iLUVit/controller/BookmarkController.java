package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark-main")
    public BookmarkMainDTO search(@Login Long userId) {
        return bookmarkService.search(userId);
    }

    @PostMapping("/bookmark/{board_id}")
    public void createBookmark(@Login Long userId, @PathVariable("board_id") Long boardId) {
        bookmarkService.create(userId, boardId);
    }

    @DeleteMapping("/bookmark/{bookmark_id}")
    public void deleteBookmark(@Login Long userId, @PathVariable("bookmark_id") Long bookmarkId) {
        bookmarkService.delete(userId, bookmarkId);
    }
}
