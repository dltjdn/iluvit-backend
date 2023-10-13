package FIS.iLUVit.domain.board.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.board.dto.BoardFindAllResponse;
import FIS.iLUVit.domain.board.dto.BoardCreateRequest;
import FIS.iLUVit.domain.board.dto.BoardFindStoryResponse;
import FIS.iLUVit.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public")
    public ResponseEntity<BoardFindAllResponse> getAllBoardByPublic(@Login Long userId) {
        BoardFindAllResponse boardFindAllResponse = boardService.findBoardByPublicList(userId);
        return ResponseEntity.ok(boardFindAllResponse);
    }

    /**
     *  시설 이야기 게시판 전체 조회
     */
    @GetMapping("in-center/{centerId}")
    public ResponseEntity<BoardFindAllResponse> getAllBoardByCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        BoardFindAllResponse boardFindAllResponse = boardService.findAllBoardByCenter(userId, centerId);
        return ResponseEntity.ok(boardFindAllResponse);
    }

    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    @GetMapping("home")
    public ResponseEntity<List<BoardFindStoryResponse>> getAllStory(@Login Long userId) {
        List<BoardFindStoryResponse> storyPreviewList = boardService.findStoryPreviewList(userId);
        return ResponseEntity.ok(storyPreviewList);
    }

    /**
     * 게시판 생성
     */
    @PostMapping("")
    public ResponseEntity<Long> createBoard(@Login Long userId, @RequestParam("center_id") Long centerId,
                                                       @RequestBody @Valid BoardCreateRequest boardCreateRequest) {
        Long boardId = boardService.saveNewBoard(userId, centerId, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardId);
    }

    /**
     * 게시판 삭제
    */
    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> deleteBoard(@Login Long userId, @PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(userId, boardId);
        return ResponseEntity.noContent().build();
    }

}
