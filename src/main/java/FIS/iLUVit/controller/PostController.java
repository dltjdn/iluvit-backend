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
    public Long createPost(@Login Long userId, @ModelAttribute @Validated PostCreateRequest request) {
        return postService.saveNewPost(request, userId);
    }

    /**
     *  게시글 삭제
     */
    @DeleteMapping("{postId}")
    public Long deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.deletePost(postId, userId);
    }

    /**
     * 내가 쓴 게시글 전체 조회
     */
    @GetMapping("mypage")
    public Slice<PostResponse> getPostByUser(@Login Long userId, Pageable pageable) {
        return postService.findPostByUser(userId, pageable);
    }

    /**
     * 장터글 끌어올리기
     */
    @PutMapping("{postId}/update")
    public void pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.pullUpPost(userId, postId);
    }

    /**
     * 게시글 제목+내용 검색 ( [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 통합 검색 )
     */
    @GetMapping("search/all")
    public Slice<PostResponse> getPost(@Login Long userId,
                                       @RequestParam("input") String keyword,
                                       Pageable pageable) {
        return postService.searchPost(keyword, userId, pageable);
    }

    /**
     * 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    @GetMapping("search/in-center")
    public Slice<PostResponse> getPostByCenter(
            @Login Long userId,
            @RequestParam("center_id") Long centerId,
            @RequestParam("input") String keyword,
            @RequestParam("auth") Auth auth,
            Pageable pageable) {
        return postService.searchPostByCenter(centerId, keyword, auth, userId, pageable);
    }

    /**
     * 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    @GetMapping("search/in-board")
    public Slice<PostResponse> getPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam("input") String keyword,
            Pageable pageable) {
        return postService.searchByBoard(boardId, keyword, pageable);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public-main")
    public List<BoardPreviewDto> getBoardDetailsByPublic(@Login Long userId) {
        return postService.findBoardDetailsByPublic(userId);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    @GetMapping("center-main")
    public List<BoardPreviewDto> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        return postService.findBoardDetailsByCenter(userId, centerId);
    }

    /**
     * HOT 게시판 게시글 전체 조회
     */
    @GetMapping("search/hot-board")
    public Slice<PostResponse> getPostByHotBoard(@RequestParam(value = "center_id", required = false) Long centerId, Pageable pageable) {
        return postService.findPostByHeartCnt(centerId, pageable);
    }

    /**
     *  게시글 상세 조회
     */
    @GetMapping("{postId}")
    public PostDetailResponse getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.findPostByPostId(userId, postId);
    }

}
