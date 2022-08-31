package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * 작성자: 이창윤
     * 작성시간: 2022/06/27 11:31 AM
     * 내용: multipart/form-data 형식으로 변환된 request, 이미지 파일 리스트 images 파라미터로 게시글 저장
     */
    @PostMapping(value = "/user/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long registerPost(@Login Long userId,
                             @RequestPart(required = false) List<MultipartFile> images,
                             @RequestPart @Validated PostRegisterRequest request) {
        return postService.savePost(request, images, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:14 PM
        내용: 게시글 삭제
    */
    @DeleteMapping("/user/post/{post_id}")
    public Long deletePost(@Login Long userId, @PathVariable("post_id") Long postId) {
        return postService.deleteById(postId, userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:14 PM
        내용: 게시글 1개 조회(게시글 자세히 보기)
    */
    @GetMapping("/post/{post_id}")
    public GetPostResponse getPost(@Login Long userId, @PathVariable("post_id") Long postId) {
        return postService.findById(userId, postId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:18 PM
        내용: 게시글 제목+내용 검색(전체 게시판[모게 + 속한 센터] 검색)
            input -> 제목 + 내용 검색 키워드
            auth -> 유저 권한
    */
    @GetMapping("/user/post/all/search")
    public Slice<GetPostResponsePreview> searchPost(@Login Long userId,
                                                    @RequestParam("input") String input,
                                                    Pageable pageable) {
        return postService.searchByKeyword(input, userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:24 PM
        내용: 게시글 제목+내용+센터 검색 (각 센터 별 검색)
    */
    @GetMapping("/post/search/inCenter")
    public Slice<GetPostResponsePreview> searchPostByCenter(
            @Login Long userId,
            @ModelAttribute PostSearchRequestDTO requestDTO,
            Pageable pageable) {
        return postService.searchByKeywordAndCenter(requestDTO.getCenter_id(), requestDTO.getInput()
                , requestDTO.getAuth(), userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:25 PM
        내용: 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
    */
    @GetMapping("/post/search/inBoard")
    public Slice<GetPostResponsePreview> searchPostByBoard(
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
    @GetMapping("/post/search/hotBoard")
    public Slice<GetPostResponsePreview> searchHotPosts(
            @RequestParam(value = "center_id", required = false) Long centerId,
            Pageable pageable) {
        return postService.findByHeartCnt(centerId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:32 PM
        내용: 내가 쓴 글 리스트
    */
    @GetMapping("/user/post/mypage")
    public PostList searchPostByUser(@Login Long userId,
                                     Pageable pageable) {
        return postService.searchByUser(userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:32 PM
        내용: 모두의 이야기 글 리스트 불러오기
    */
    @GetMapping("/post/modu-main")
    public List<BoardPreview> searchMainPreview(@Login Long userId) {

        return postService.searchMainPreview(userId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:33 PM
        내용: 유치원별 이야기 글 리스트 불러오기
    */
    @GetMapping("/user/post/center-main")
    public List<BoardPreview> searchCenterMainPreview(@Login Long userId, @RequestParam("center_id") Long centerId) {
        return postService.searchCenterMainPreview(userId, centerId);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/27 1:34 PM
        내용: 장터글 끌어올리기
    */
    @PutMapping("/post/update/{post_id}")
    public void pullUp(@Login Long userId, @PathVariable("post_id") Long postId) {
        postService.updateDate(userId, postId);
    }


}
