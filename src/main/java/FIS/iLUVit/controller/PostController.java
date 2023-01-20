package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
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
     작성자: 이창윤
     작성시간: 2022/06/27 1:32 PM
     내용: 내가 쓴 글 리스트
     */
    @GetMapping("mypage")
    public PostListDto searchPostByUser(@Login Long userId,
                                        Pageable pageable) {
        return postService.searchByUser(userId, pageable);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:18 PM
     내용: 게시글 제목+내용 검색(전체 게시판[모게 + 속한 센터] 검색)
     input -> 제목 + 내용 검색 키워드
     auth -> 유저 권한
     */
    @GetMapping("search/all")
    public Slice<PostPreviewResponse> searchPost(@Login Long userId,
                                                 @RequestParam("input") String input,
                                                 Pageable pageable) {
        return postService.searchByKeyword(input, userId, pageable);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:32 PM
     내용: 모두의 이야기 글 리스트 불러오기
     */
    @GetMapping("public-main")
    public List<BoardPreviewDto> searchMainPreview(@Login Long userId) {

        return postService.searchMainPreview(userId);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:24 PM
     내용: 게시글 제목+내용+센터 검색 (각 센터 별 검색)
     */
    @GetMapping("search/in-center")
    public Slice<PostPreviewResponse> searchPostByCenter(
            @Login Long userId,
            @ModelAttribute PostSearchRequest requestDTO,
            Pageable pageable) {
        return postService.searchByKeywordAndCenter(requestDTO.getCenter_id(), requestDTO.getInput()
                , requestDTO.getAuth(), userId, pageable);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:25 PM
     내용: 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    @GetMapping("search/in-board")
    public Slice<PostPreviewResponse> searchPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam(value = "input", required = false) String input,
            Pageable pageable) {
        return postService.searchByKeywordAndBoard(boardId, input, pageable);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:30 PM
     내용: HOT 게시판 글 목록 조회
     */
    @GetMapping("search/hot-board")
    public Slice<PostPreviewResponse> searchHotPosts(
            @RequestParam(value = "center_id", required = false) Long centerId,
            Pageable pageable) {
        return postService.findByHeartCnt(centerId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성시간: 2022/06/27 11:31 AM
     * 내용: multipart/form-data 형식으로 변환된 request, 이미지 파일 리스트 images 파라미터로 게시글 저장
     */
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long registerPost(@Login Long userId,
                             @RequestPart(required = false) List<MultipartFile> images,
                             @RequestPart @Validated PostRequest request) {
        return postService.savePost(request, images, userId);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:14 PM
     내용: 게시글 삭제
     */
    @DeleteMapping("{postId}")
    public Long deletePost(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.deleteById(postId, userId);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/27 1:14 PM
     내용: 게시글 1개 조회(게시글 자세히 보기)
     */
    @GetMapping("{postId}")
    public PostResponse getPost(@Login Long userId, @PathVariable("postId") Long postId) {
        return postService.findById(userId, postId);
    }

    /**
     * 작성자: 이창윤
     * 작성시간: 2022/06/27 11:31 AM
     * 내용: 게시글 저장 리액트네이티브
     */
    @PostMapping(value = "react-native", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long registerPostTemp(@Login Long userId,
                             @RequestPart(required = false) List<MultipartFile> images,
                             @ModelAttribute("request") @Validated PostRequest request) {
        log.info("PostRegisterRequest = {}", request);
        return postService.savePost(request, images, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:33 PM
        내용: 유치원별 이야기 글 리스트 불러오기
    */
    @GetMapping("center-main")
    public List<BoardPreviewDto> searchCenterMainPreview(@Login Long userId, @RequestParam("center_id") Long centerId) {
        return postService.searchCenterMainPreview(userId, centerId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:34 PM
        내용: 장터글 끌어올리기
    */
    @PutMapping("{postId}/update")
    public void pullUp(@Login Long userId, @PathVariable("postId") Long postId) {
        postService.updateDate(userId, postId);
    }
}
