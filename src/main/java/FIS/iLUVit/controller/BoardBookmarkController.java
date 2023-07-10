package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
//import FIS.iLUVit.controller.dto.BoardBookmarkDto;
import FIS.iLUVit.dto.board.StoryDto;
import FIS.iLUVit.service.BoardBookmarkService;
import lombok.RequiredArgsConstructor;
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
     * 작성자: 이창윤
     * 작성내용: 즐겨찾는 게시판 전체 조회
     * 비고: 게시글 목록 한번에 불러오기. 메인 페이지에서 유저의 모든 이야기에서 즐겨찾는 게시판에서 최신 글 하나씩 엮어서 보여줌.
            + 게시글 목록 한번에 불러오기 (비회원 전용)
    */
    @GetMapping("main")
    public List<StoryDto> getAllBoardBookmark(@Login Long userId) {
        return boardBookmarkService.findBoardBookmarkByUser(userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 즐겨찾는 게시판 등록
    */
    @PostMapping("{boardId}")
    public Long createBoardBookmark(@Login Long userId, @PathVariable("boardId") Long boardId) {
        return boardBookmarkService.saveBoardBookmark(userId, boardId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 즐겨찾는 게시판 삭제
    */
    @DeleteMapping("{bookmarkId}")
    public Long deleteBoardBookmark(@Login Long userId, @PathVariable("bookmarkId") Long bookmarkId) {
        return boardBookmarkService.deleteBoardBookmark(userId, bookmarkId);
    }

}
