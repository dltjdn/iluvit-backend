package FIS.iLUVit.domain.boardbookmark.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.boardbookmark.dto.BoardBookmarkFindResponse;
import FIS.iLUVit.domain.boardbookmark.service.BoardBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("board-bookmark")
public class BoardBookmarkController {

    private final BoardBookmarkService boardBookmarkService;

    /**
     * COMMON
     */

    /**
     * 즐겨찾는 게시판 전체 조회
    */
    @GetMapping("main")
    public ResponseEntity<List<BoardBookmarkFindResponse>> getAllBoardBookmark(@Login Long userId) {
        List<BoardBookmarkFindResponse> responses = boardBookmarkService.findBoardBookmarkByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 즐겨찾는 게시판 등록
    */
    @PostMapping("{boardId}")
    public ResponseEntity<Long> createBoardBookmark(@Login Long userId, @PathVariable("boardId") Long boardId) {
        Long response = boardBookmarkService.saveBoardBookmark(userId, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 즐겨찾는 게시판 삭제
    */
    @DeleteMapping("{bookmarkId}")
    public ResponseEntity<Void> deleteBoardBookmark(@Login Long userId, @PathVariable("bookmarkId") Long bookmarkId) {
        boardBookmarkService.deleteBoardBookmark(userId, bookmarkId);
        return ResponseEntity.noContent().build();
    }
}
