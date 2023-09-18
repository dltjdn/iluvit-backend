package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.board.BoardListDto;
import FIS.iLUVit.dto.board.BoardRequest;
import FIS.iLUVit.dto.board.StoryPreviewDto;
import FIS.iLUVit.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시판 API")
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 이야기 전체 조회
     */
    @Operation(summary = "이야기 전체 조회", description = "이야기 홈에서 전체 게시판을 띄워줍니다.")
    @GetMapping("home")
    public List<StoryPreviewDto> getAllStory(@Login Long userId) {
        return boardService.findStoryPreviewList(userId);
    }


    /**
     * 작성자: 이창윤
     * 작성내용: 모두의 이야기 게시판 전체 조회
     */
    @Operation(summary = "모두의 이야기 게시판 전체 조회", description = "모두의 이야기 게시판 목록 전체를 조회합니다.")
    @GetMapping("public")
    public BoardListDto getAllBoardByPublic(@Login Long userId) {
        return boardService.findBoardByPublicList(userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 시설 이야기 게시판 전체 조회
     */
    @Operation(summary = "시설 이야기 게시판 전체 조회", description = "시설 이야기 게시판 목록 전체를 조회합니다.")
    @GetMapping("in-center/{centerId}")
    public BoardListDto getAllBoardByCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        return boardService.findAllBoardByCenter(userId, centerId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시판 생성
     */
    @Operation(summary = "게시판 생성", description = "게시판을 생성합니다.")
    @PostMapping("")
    public Long createBoard(@Login Long userId,
                            @RequestParam(value = "center_id", required = false) Long center_id,
                            @RequestBody @Valid BoardRequest request) {
        return boardService.saveNewBoard(userId, center_id, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시판 삭제
    */
    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다.(시설게시판만 삭제 가능)")
    @DeleteMapping("{boardId}")
    public Long deleteBoard(@Login Long userId, @PathVariable("boardId") Long boardId) {
        return boardService.deleteBoardWithValidation(userId, boardId);
    }

}
