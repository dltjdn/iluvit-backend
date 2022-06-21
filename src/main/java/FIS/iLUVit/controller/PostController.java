package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void registerPost(@Login Long userId,
                             @RequestPart PostRegisterRequest request,
                             @RequestPart(required = false) List<MultipartFile> images) {
        postService.savePost(request, images, userId);
    }

    @DeleteMapping("/post/{post_id}")
    public void deletePost(@Login Long userId, @PathVariable("post_id") Long postId) {
        postService.deleteById(postId, userId);
    }

    @GetMapping("/post/{post_id}")
    public GetPostResponse getPost(@PathVariable("post_id") Long postId) {
        return postService.findById(postId);
    }

    @GetMapping("/post/all/search")
    public Slice<GetPostResponsePreview> searchPost(@Login Long userId,
                                                    @RequestParam("input") String input,
                                                    @RequestParam("auth") Auth auth,
                                                    Pageable pageable) {
        return postService.searchByKeyword(input, auth, userId, pageable);
    }

    @GetMapping("/post/search/inCenter")
    public Slice<GetPostResponsePreview> searchPostByCenter(
            @Login Long userId,
            @ModelAttribute PostSearchRequestDTO requestDTO,
            Pageable pageable) {
        return postService.searchByKeywordAndCenter(requestDTO.getCenter_id(), requestDTO.getInput()
                , requestDTO.getAuth(), userId, pageable);
    }

    @GetMapping("/post/search/inBoard")
    public Slice<GetPostResponsePreview> searchPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam(value = "input", required = false) String input,
            Pageable pageable) {
        return postService.searchByKeywordAndBoard(boardId, input, pageable);
    }

    @GetMapping("/post/mypage")
    public PostList searchPostByUser(@Login Long userId,
                                     Pageable pageable) {
        return postService.searchByUser(userId, pageable);
    }

    @GetMapping("/post/modu-main")
    public List<BoardPreview> searchMainPreview(@Login Long userId) {
        return postService.searchMainPreview(userId);
    }

    @GetMapping("/post/center-main")
    public List<BoardPreview> searchCenterMainPreview(@Login Long userId,
                                                      @RequestParam("center_id") Long centerId) {
        return postService.searchCenterMainPreview(userId, centerId);
    }
}
