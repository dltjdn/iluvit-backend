package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.dto.post.PostRequest;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 API")
@RequestMapping("post")
public class PostController {

    private final PostService postService;

    /**
     * COMMON
     */

//    /**
//     * 작성자: 이창윤
//     * 작성시간: 2022/06/27 11:31 AM
//     * 내용: multipart/form-data 형식으로 변환된 request, 이미지 파일 리스트 images 파라미터로 게시글 저장
//     */
//    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public Long registerPost(@Login Long userId,
//                             @RequestPart(required = false) List<MultipartFile> images,
//                             @RequestPart @Validated PostRequest request) {
//        return postService.savePost(request, images, userId);
//    }

    /**
     * 작성자: 이창윤
     * 내용: 게시글 저장
     */
    @Operation(summary = "게시글 저장", description = "게시글을 저장합니다.")
    @PostMapping("")
    public Long createPost(@Login Long userId,
                                 @ModelAttribute @Validated PostRequest request) {
        return postService.saveNewPost(request, userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 삭제
     */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("{postId}")
    public Long deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.deletePost(postId, userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 내가 쓴 게시글 전체 조회
     */
    @Operation(summary = "내가 쓴 게시글 전체 조회", description = "내가 쓴 글의 목록을 조회합니다.")
    @GetMapping("mypage")
    public Slice<PostPreviewDto> getPostByUser(@Login Long userId, Pageable pageable) {
        return postService.findPostByUser(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 내용: 장터글 끌어올리기
     * 비고: 현재 시간으로 업데이트
     */
    @Operation(summary = "장터글 끌어올리기", description = "장터글을 끌어올립니다.(게시글 현재시간으로 업데이트)")
    @PutMapping("{postId}/update")
    public void pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.pullUpPost(userId, postId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용 검색(전체 게시판[모게 + 속한 시설] 검색)
     * 비고: input -> 제목 + 내용 검색 키워드, auth -> 유저 권한
     */
    @Operation(summary = "[모두의 이야기 + 유저가 속한 센터의 이야기] 에서 게시글 검색", description = "[모두의 이야기 + 유저가 속한 센터의 이야기] 에서 게시글 제목+내용을 검색합니다.")
    @GetMapping("search/all")
    public Slice<PostPreviewDto> getPost(@Login Long userId,
                                            @RequestParam("input") String keyword,
                                            Pageable pageable) {
        return postService.searchPost(keyword, userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    @Operation(summary = "[시설 이야기] or [모두의 이야기] 에서 게시글 검색", description = "[시설 이야기] or [모두의 이야기] 에서 게시글 제목+내용을 검색합니다.")
    @GetMapping("search/in-center")
    public Slice<PostPreviewDto> getPostByCenter(
            @Login Long userId,
            @RequestParam("center_id") Long centerId,
            @RequestParam("input") String keyword,
            @RequestParam("auth") Auth auth,
            Pageable pageable) {
        return postService.searchPostByCenter(centerId, keyword, auth, userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    @Operation(summary = "각 게시판 별 게시글 검색", description = "각 게시판 별 게시글 제목+내용을 검색합니다.")
    @GetMapping("search/in-board")
    public Slice<PostPreviewDto> getPostByBoard(@Login Long userId, @RequestParam("board_id") Long boardId,
                                                @RequestParam("input") String keyword, Pageable pageable) {
        return postService.searchByBoard(userId, boardId, keyword, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 모두의 이야기 게시판 전체 조회
     */
    @Operation(summary = "모두의 이야기 게시판 전체 조회", description = "모두의 이야기 글 목록을 불러옵니다.")
    @GetMapping("public-main")
    public List<BoardPreviewDto> getBoardDetailsByPublic(@Login Long userId) {
        return postService.findBoardDetailsByPublic(userId);
    }

    /**
     * 작성자: 이창윤
     * 직성내용: 시설별 이야기 게시판 전체 조회
     */
    @Operation(summary = "시설별 이야기 게시판 전체 조회", description = "시설별 이야기 글 목록을 불러옵니다.")
    @GetMapping("center-main")
    public List<BoardPreviewDto> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        return postService.findBoardDetailsByCenter(userId, centerId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: HOT 게시판 게시글 전체 조회
     */
    @Operation(summary = "HOT 게시판 게시글 전체 조회", description = "HOT 게시판 글 목록을 조회합니다.")
    @GetMapping("search/hot-board")
    public Slice<PostPreviewDto> getPostByHotBoard(@Login Long userId, @RequestParam(value = "center_id", required = false) Long centerId, Pageable pageable) {
        return postService.findPostByHeartCnt(userId, centerId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 상세 조회
     */
    @Operation(summary = "게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    @GetMapping("{postId}")
    public PostResponse getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.findPostByPostId(userId, postId);
    }

}
