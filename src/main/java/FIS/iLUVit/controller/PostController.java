package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.dto.post.PostCreateRequest;
import FIS.iLUVit.dto.post.PostDetailResponse;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("post")
public class PostController {

    private final PostService postService;

    /**
     * COMMON
     */

    /**
     * 게시글 저장
     */
    @PostMapping("")
    public ResponseEntity<Void> createPost(@Login Long userId, @ModelAttribute @Validated PostCreateRequest postCreateRequest) {
        postService.saveNewPost(userId, postCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *  게시글 삭제
     */
    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 내가 쓴 게시글 전체 조회
     */
    @GetMapping("mypage")
    public ResponseEntity<Slice<PostResponse>> getPostByUser(@Login Long userId, Pageable pageable) {
        Slice<PostResponse> postResponse = postService.findPostByUser(userId, pageable);
        return ResponseEntity.ok(postResponse);
    }

    /**
     * 장터글 끌어올리기
     */
    @PutMapping("{postId}/update")
    public ResponseEntity<Void> pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.pullUpPost(userId, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 게시글 제목+내용 검색 ( [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 통합 검색 )
     */
    @GetMapping("search/all")
    public ResponseEntity<Slice<PostResponse>> getPost(@Login Long userId, @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchPost(keyword, userId, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     * 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    @GetMapping("search/in-center")
    public ResponseEntity<Slice<PostResponse>> getPostByCenter(@Login Long userId, @RequestParam("center_id") Long centerId,
            @RequestParam("input") String keyword, @RequestParam("auth") Auth auth, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchPostByCenter(centerId, keyword, auth, userId, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     * 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    @GetMapping("search/in-board")
    public ResponseEntity<Slice<PostResponse>> getPostByBoard(@RequestParam("board_id") Long boardId, @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchByBoard(boardId, keyword, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public-main")
    public ResponseEntity<List<BoardPreviewDto>> getBoardDetailsByPublic(@Login Long userId) {
        List<BoardPreviewDto> boardPreviewDtos = postService.findBoardDetailsByPublic(userId);
        return ResponseEntity.ok(boardPreviewDtos);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    @GetMapping("center-main")
    public ResponseEntity<List<BoardPreviewDto>> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        List<BoardPreviewDto> boardPreviewDtos = postService.findBoardDetailsByCenter(userId, centerId);
        return ResponseEntity.ok(boardPreviewDtos);
    }

    /**
     * HOT 게시판 게시글 전체 조회
     */
    @GetMapping("search/hot-board")
    public ResponseEntity<Slice<PostResponse>> getPostByHotBoard(@RequestParam(value = "center_id", required = false) Long centerId, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.findPostByHeartCnt(centerId, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     *  게시글 상세 조회
     */
    @GetMapping("{postId}")
    public PostDetailResponse getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.findPostByPostId(userId, postId);
    }

}
