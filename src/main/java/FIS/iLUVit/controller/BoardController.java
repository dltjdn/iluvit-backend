package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.board.BoardIdDto;
import FIS.iLUVit.dto.board.BoardListDto;
import FIS.iLUVit.dto.board.BoardRequest;
import FIS.iLUVit.dto.board.StoryPreviewDto;
import FIS.iLUVit.service.BoardService;
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
    public ResponseEntity<BoardListDto> getAllBoardByPublic(@Login Long userId) {
        BoardListDto boardListDto = boardService.findBoardByPublicList(userId);
        return ResponseEntity.ok(boardListDto);
    }

    /**
     *  시설 이야기 게시판 전체 조회
     */
    @GetMapping("in-center/{centerId}")
    public ResponseEntity<BoardListDto> getAllBoardByCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        BoardListDto boardListDto = boardService.findAllBoardByCenter(userId, centerId);
        return ResponseEntity.ok(boardListDto);
    }

    /**
     * 이야기 (모두의 이야기 + 유저가 속한 시설의 이야기) 전체 조회
     */
    @GetMapping("home")
    public ResponseEntity<List<StoryPreviewDto>> getAllStory(@Login Long userId) {
        List<StoryPreviewDto> storyPreviewList = boardService.findStoryPreviewList(userId);
        return ResponseEntity.ok(storyPreviewList);
    }

    /**
     * 게시판 생성
     */
    @PostMapping("")
    public ResponseEntity<BoardIdDto> createBoard(@Login Long userId,
                                                  @RequestParam(value = "center_id", required = false) Long center_id,
                                                  @RequestBody @Valid BoardRequest request) {
        BoardIdDto boardIdDto = boardService.saveNewBoard(userId, center_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardIdDto);
    }

    /**
     * 게시판 삭제
    */
    @DeleteMapping("{boardId}")
    public ResponseEntity<Void> deleteBoard(@Login Long userId, @PathVariable("boardId") Long boardId) {
        boardService.deleteBoardWithValidation(userId, boardId);
        return ResponseEntity.noContent().build();
    }

}
