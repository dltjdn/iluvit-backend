package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.BoardListDTO;
import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/modu")
    public BoardListDTO searchAllBoard(@Login Long userId) {
        return boardService.findAllWithBookmark(userId);
    }

    @GetMapping("/board/inCenter/{center_id}")
    public BoardListDTO searchAllInCenter(@Login Long userId, @PathVariable("center_id") Long centerId) {
        return boardService.findAllWithBookmarkInCenter(userId, centerId);
    }

    @PostMapping("/board")
    public void createBoard(@RequestParam(value = "center_id", required = false) Long center_id,
                            @RequestBody CreateBoardRequest request) {
        boardService.create(center_id, request);
    }
}
