package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.board.BoardBookmarkIdDto;
import FIS.iLUVit.dto.board.StoryDto;
import FIS.iLUVit.service.BoardBookmarkService;
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
    public ResponseEntity<List<StoryDto>> getAllBoardBookmark(@Login Long userId) {
        List<StoryDto> storyDtoList = boardBookmarkService.findBoardBookmarkByUser(userId);
        return ResponseEntity.ok(storyDtoList);
    }

    /**
     * 즐겨찾는 게시판 등록
    */
    @PostMapping("{boardId}")
    public ResponseEntity<BoardBookmarkIdDto> createBoardBookmark(@Login Long userId, @PathVariable("boardId") Long boardId) {
        BoardBookmarkIdDto boardBookmarkIdDto = boardBookmarkService.saveBoardBookmark(userId, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardBookmarkIdDto);
    }

    /**
     * 즐겨찾는 게시판 삭제
    */
    @DeleteMapping("{bookmarkId}")
    public ResponseEntity<Void> deleteBoardBookmark(@Login Long userId, @PathVariable("bookmarkId") Long bookmarkId) {
        boardBookmarkService.deleteBoardBookmark(userId, bookmarkId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
