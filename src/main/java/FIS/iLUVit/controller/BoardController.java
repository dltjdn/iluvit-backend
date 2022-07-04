package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.BoardListDTO;
import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.service.BoardService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    
    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:39 PM
        내용: 모두의 이야기 내 게시판 목록 조회
    */
    @GetMapping("/board/modu")
    public BoardListDTO searchAllBoard(@Login Long userId) {
        return boardService.findAllWithBookmark(userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:48 PM
        내용: 시설(유치원) 내 게시판 목록 조회
    */
    @GetMapping("/board/inCenter/{center_id}")
    public BoardListDTO searchAllInCenter(@Login Long userId, @PathVariable("center_id") Long centerId) {
        return boardService.findAllWithBookmarkInCenter(userId, centerId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 2:51 PM
        내용: 게시판 생성 - center_id 값 유무에 따라 모두/시설 이야기 내 게시판 생성
    */
    @PostMapping("/board")
    public Long createBoard(@RequestParam(value = "center_id", required = false) Long center_id,
                            @RequestBody CreateBoardRequest request) {
        return boardService.create(center_id, request);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 4:53 PM
        내용: 게시판 삭제
    */
    @DeleteMapping("/board/{board_id}")
    public Long deleteBoard(@Login Long userId, @PathVariable("board_id") Long boardId) {
        return boardService.remove(userId, boardId);
    }
}
