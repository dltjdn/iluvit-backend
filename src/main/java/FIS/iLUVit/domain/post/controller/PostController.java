package FIS.iLUVit.domain.post.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.post.dto.BoardPreviewResponse;
import FIS.iLUVit.domain.post.dto.PostResponse;
import FIS.iLUVit.domain.post.dto.PostCreateRequest;
import FIS.iLUVit.domain.post.dto.PostDetailResponse;
import FIS.iLUVit.domain.post.service.PostService;
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
        return ResponseEntity.noContent().build();
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
     * [모두의 이야기 + 유저가 속한 센터의 이야기] 에서  게시글 제목+내용 검색
     */
    @GetMapping("search/all")
    public ResponseEntity<Slice<PostResponse>> getPost(@Login Long userId, @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchPost(userId, keyword, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     * [시설 이야기] or [모두의 이야기] 에서 게시글 제목+내용 검색
     */
    @GetMapping("search/in-center")
    public ResponseEntity<Slice<PostResponse>> getPostByCenter(@Login Long userId, @RequestParam("center_id") Long centerId,
                                                               @RequestParam("input") String keyword, @RequestParam("auth") String auth, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchPostByCenter( userId, centerId, keyword, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     * 각 게시판 별 게시글 제목+내용 검색
     */
    @GetMapping("search/in-board")
    public ResponseEntity<Slice<PostResponse>> getPostByBoard(@Login Long userId, @RequestParam("board_id") Long boardId,
                                                              @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.searchByBoard(userId, boardId, keyword, pageable);
        return ResponseEntity.ok(postResponses);
    }


    /**
     * HOT 게시판 게시글 전체 조회
     */
    @GetMapping("search/hot-board")
    public ResponseEntity<Slice<PostResponse>> getPostByHotBoard(@Login Long userId, @RequestParam("center_id") Long centerId, Pageable pageable) {
        Slice<PostResponse> postResponses = postService.findPostByHeartCnt(userId, centerId, pageable);
        return ResponseEntity.ok(postResponses);
    }

    /**
     *  게시글 상세 조회
     */
    @GetMapping("{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        PostDetailResponse postDetailResponse = postService.findPostByPostId(userId, postId);
        return ResponseEntity.ok(postDetailResponse);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public-main")
    public ResponseEntity<List<BoardPreviewResponse>> getBoardDetailsByPublic(@Login Long userId) {
        List<BoardPreviewResponse> boardPreviewResponses = postService.findBoardDetailsByPublic(userId);
        return ResponseEntity.ok(boardPreviewResponses);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    @GetMapping("center-main")
    public ResponseEntity<List<BoardPreviewResponse>> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        List<BoardPreviewResponse> boardPreviewResponses = postService.findBoardDetailsByCenter(userId, centerId);
        return ResponseEntity.ok(boardPreviewResponses);
    }


    /**
     * 장터글 끌어올리기
     */
    @PutMapping ("{postId}/update")
    public ResponseEntity<Void> pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.pullUpPost(userId, postId);
        return ResponseEntity.noContent().build();
    }


}
