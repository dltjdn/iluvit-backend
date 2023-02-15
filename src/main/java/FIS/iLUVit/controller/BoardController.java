package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.board.BoardListDto;
import FIS.iLUVit.dto.board.BoardRequest;
import FIS.iLUVit.dto.board.StoryPreviewDto;
import FIS.iLUVit.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 내용: 이야기 홈에서 센터의 게시판 띄워주기
     */
    @GetMapping("home")
    public List<StoryPreviewDto> getStoryHome(@Login Long userId) {
        return boardService.findCenterStory(userId);
    }


    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:39 PM
        내용: 모두의 이야기 게시판 목록 조회
    */
    @GetMapping("public")
    public BoardListDto searchAllBoard(@Login Long userId) {
        return boardService.findAllWithBookmark(userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:48 PM
        내용: 유치원 이야기 게시판 목록 조회
    */
    @GetMapping("in-center/{centerId}")
    public BoardListDto searchAllInCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        return boardService.findAllWithBookmarkInCenter(userId, centerId);
    }

    /**
     * 작성자: 이창윤
     * 작성시간: 2022/06/24 2:51 PM
     * 내용: 게시판 생성
     */
    @PostMapping("")
    public Long createBoard(@Login Long userId,
                            @RequestParam(value = "center_id", required = false) Long center_id,
                            @RequestBody @Valid BoardRequest request) {
        return boardService.create(userId, center_id, request);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 4:53 PM
        내용: 게시판 삭제
    */
    @DeleteMapping("{boardId}")
    public Long deleteBoard(@Login Long userId, @PathVariable("boardId") Long boardId) {
        return boardService.remove(userId, boardId);
    }

}
