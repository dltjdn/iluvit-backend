package FIS.iLUVit.domain.post.controller;

import FIS.iLUVit.domain.post.dto.PostFindByBoardResponse;
import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.post.dto.PostFindResponse;
import FIS.iLUVit.domain.post.dto.PostCreateRequest;
import FIS.iLUVit.domain.post.dto.PostFindDetailResponse;
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
    public ResponseEntity<Long> createPost(@Login Long userId, @ModelAttribute @Validated PostCreateRequest postCreateRequest) {
        Long response = postService.saveNewPost(userId, postCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     *  게시글 삭제
     */
    @DeleteMapping("{postId}")
    public ResponseEntity<Long> deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        Long response = postService.deletePost(postId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 쓴 게시글 전체 조회
     */
    @GetMapping("mypage")
    public ResponseEntity<Slice<PostFindResponse>> getPostByUser(@Login Long userId, Pageable pageable) {
        Slice<PostFindResponse> responses = postService.findPostByUser(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * [모두의 이야기 + 유저가 속한 센터의 이야기] 에서  게시글 제목+내용 검색
     */
    @GetMapping("search/all")
    public ResponseEntity<Slice<PostFindResponse>> getPost(@Login Long userId, @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostFindResponse> responses = postService.searchPost(userId, keyword, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * [시설 이야기] or [모두의 이야기] 에서 게시글 제목+내용 검색
     */
    @GetMapping("search/in-center")
    public ResponseEntity<Slice<PostFindResponse>> getPostByCenter(@Login Long userId, @RequestParam("center_id") Long centerId,
                                                                   @RequestParam("input") String keyword, @RequestParam("auth") String auth, Pageable pageable) {
        Slice<PostFindResponse> responses = postService.searchPostByCenter( userId, centerId, keyword, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 각 게시판 별 게시글 제목+내용 검색
     */
    @GetMapping("search/in-board")
    public ResponseEntity<Slice<PostFindResponse>> getPostByBoard(@Login Long userId, @RequestParam("board_id") Long boardId,
                                                                  @RequestParam("input") String keyword, Pageable pageable) {
        Slice<PostFindResponse> responses = postService.searchByBoard(userId, boardId, keyword, pageable);
        return ResponseEntity.ok(responses);
    }


    /**
     * HOT 게시판 게시글 전체 조회
     */
    @GetMapping("search/hot-board")
    public ResponseEntity<Slice<PostFindResponse>> getPostByHotBoard(@Login Long userId, @RequestParam("center_id") Long centerId, Pageable pageable) {
        Slice<PostFindResponse> responses = postService.findPostByHeartCnt(userId, centerId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     *  게시글 상세 조회
     */
    @GetMapping("{postId}")
    public ResponseEntity<PostFindDetailResponse> getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        PostFindDetailResponse response = postService.findPostByPostId(userId, postId);
        return ResponseEntity.ok(response);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public-main")
    public ResponseEntity<List<PostFindByBoardResponse>> getBoardDetailsByPublic(@Login Long userId) {
        List<PostFindByBoardResponse> responses = postService.findBoardDetailsByPublic(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    @GetMapping("center-main")
    public ResponseEntity<List<PostFindByBoardResponse>> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        List<PostFindByBoardResponse> responses = postService.findBoardDetailsByCenter(userId, centerId);
        return ResponseEntity.ok(responses);
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
