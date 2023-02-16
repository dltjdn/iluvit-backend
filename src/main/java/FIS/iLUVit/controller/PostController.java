package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.dto.post.PostRequest;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.dto.post.PostSearchRequest;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    /**
//     * 작성자: 이창윤
//     * 작성내용: 게시글 저장
//     * 비고: multipart/form-data 형식으로 변환된 request, 이미지 파일 리스트 images 파라미터로 게시글 저장
//     */
//    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
////    @ResponseStatus(HttpStatus.ACCEPTED)
//    public Long createPost(@Login Long userId,
//                             @RequestPart(required = false) List<MultipartFile> images,
//                             @RequestPart @Validated PostRequest request) {
//        return postService.savePost(request, images, userId);
//    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 저장
     */
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long createPost(@Login Long userId, @RequestPart(required = false) List<MultipartFile> images,
                                 @ModelAttribute("request") @Validated PostRequest request) {
        log.info("PostRegisterRequest = {}", request);
        return postService.savePost(request, images, userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 삭제
     */
    @DeleteMapping("{postId}")
    public Long deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.deleteById(postId, userId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 내가 쓴 게시글 전체 조회
     */
    @GetMapping("mypage")
    public Slice<PostPreviewDto> getPostByUser(@Login Long userId, Pageable pageable) {
        return postService.searchByUser(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 내용: 장터글 끌어올리기
     * 비고: 현재 시간으로 업데이트
     */
    @PutMapping("{postId}/update")
    public void pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.updateDate(userId, postId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용 검색(전체 게시판[모게 + 속한 시설] 검색)
     * 비고: input -> 제목 + 내용 검색 키워드, auth -> 유저 권한
     */
    @GetMapping("search/all")
    public Slice<PostPreviewDto> getPost(@Login Long userId,
                                            @RequestParam("input") String input,
                                            Pageable pageable) {
        return postService.searchByKeyword(input, userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    @GetMapping("search/in-center")
    public Slice<PostPreviewDto> getPostByCenter(
            @Login Long userId,
            @ModelAttribute PostSearchRequest requestDTO,
            Pageable pageable) {
        return postService.searchByKeywordAndCenter(requestDTO.getCenter_id(), requestDTO.getInput()
                , requestDTO.getAuth(), userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    @GetMapping("search/in-board")
    public Slice<PostPreviewDto> getPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam(value = "input", required = false) String input,
            Pageable pageable) {
        return postService.searchByKeywordAndBoard(boardId, input, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 모두의 이야기 게시판 전체 조회
     */
    @GetMapping("public-main")
    public List<BoardPreviewDto> getBoardDetailsByPublic(@Login Long userId) {

        return postService.searchMainPreview(userId);
    }

    /**
     * 작성자: 이창윤
     * 직성내용: 시설별 이야기 게시판 전체 조회
     */
    @GetMapping("center-main")
    public List<BoardPreviewDto> getBoardDetailsByCenter(@Login Long userId, @RequestParam("center_id") Long centerId) {
        return postService.searchCenterMainPreview(userId, centerId);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: HOT 게시판 게시글 전체 조회
     */
    @GetMapping("search/hot-board")
    public Slice<PostPreviewDto> getPostByHotBoard(@RequestParam(value = "center_id", required = false) Long centerId, Pageable pageable) {
        return postService.findByHeartCnt(centerId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 게시글 상세 조회
     */
    @GetMapping("{postId}")
    public PostResponse getPostDetails(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.findById(userId, postId);
    }

}
