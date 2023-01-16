package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.BookmarkMainDTO;
import FIS.iLUVit.service.BoardBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("board-bookmark")
public class BoardBookmarkController {

    private final BoardBookmarkService boardBookmarkService;

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:57 PM
        내용: 게시글 목록 한번에 불러오기. 메인 페이지에서 유저의 모든 이야기에서 즐겨찾는 게시판에서 최신 글 하나씩 엮어서 보여줌.
            + 게시글 목록 한번에 불러오기 (비회원 전용)
    */
    @GetMapping("main")
    public BookmarkMainDTO search(@Login Long userId) {
        if (userId == null) {
            return boardBookmarkService.searchByDefault();
        }
        return boardBookmarkService.search(userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 3:08 PM
        내용: 즐겨찾는 게시판 추가
    */
    @PostMapping("{boardId}")
    public Long createBookmark(@Login Long userId, @PathVariable("boardId") Long boardId) {
        return boardBookmarkService.create(userId, boardId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 3:09 PM
        내용: 즐겨찾는 게시판 삭제
    */
    @DeleteMapping("{bookmarkId}")
    public Long deleteBookmark(@Login Long userId, @PathVariable("bookmarkId") Long bookmarkId) {
        return boardBookmarkService.delete(userId, bookmarkId);
    }
}
