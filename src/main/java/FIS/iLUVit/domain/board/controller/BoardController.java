package FIS.iLUVit.domain.board.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.board.dto.BoardIdResponse;
import FIS.iLUVit.domain.board.dto.BoardListResponse;
import FIS.iLUVit.domain.board.dto.BoardCreateRequest;
import FIS.iLUVit.domain.board.dto.BoardStoryPreviewResponse;
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
    public ResponseEntity<BoardListResponse> getAllBoardByPublic(@Login Long userId) {
        BoardListResponse boardListResponse = boardService.findBoardByPublicList(userId);
        return ResponseEntity.ok(boardListResponse);
    }

    /**
     *  시설 이야기 게시판 전체 조회
     */
    @GetMapping("in-center/{centerId}")
    public ResponseEntity<BoardListResponse> getAllBoardByCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        BoardListResponse boardListResponse = boardService.findAllBoardByCenter(userId, centerId);
        return ResponseEntity.ok(boardListResponse);
    }

    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    @GetMapping("home")
    public ResponseEntity<List<BoardStoryPreviewResponse>> getAllStory(@Login Long userId) {
        List<BoardStoryPreviewResponse> storyPreviewList = boardService.findStoryPreviewList(userId);
        return ResponseEntity.ok(storyPreviewList);
    }

    /**
     * 게시판 생성
     */
    @PostMapping("{centerId}")
    public ResponseEntity<BoardIdResponse> createBoard(@Login Long userId, @PathVariable("centerId") Long centerId,
                                                       @RequestBody @Valid BoardCreateRequest boardCreateRequest) {
        BoardIdResponse boardIdResponse = boardService.saveNewBoard(userId, centerId, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardIdResponse);
    }

    /**
     * 게시판 삭제
    */
    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> deleteBoard(@Login Long userId, @PathVariable("boardId") Long boardId) {
        boardService.deleteBoardWithValidation(userId, boardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
