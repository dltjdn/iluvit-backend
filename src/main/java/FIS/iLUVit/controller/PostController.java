package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.GetPostResponse;
import FIS.iLUVit.controller.dto.PostRegisterRequest;
import FIS.iLUVit.service.PostService;
import lombok.RequiredArgsConstructor;
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
    public List<GetPostResponse> searchPost(@RequestParam("input") String input) {
        return postService.searchByKeyword(input);
    }

    @GetMapping("/post/search/inCenter")
    public List<GetPostResponse> searchPostByCenter(
            @RequestParam("center_id") Long centerId,
            @RequestParam("input") String input) {
        return postService.searchByKeywordAndCenter(centerId, input);
    }

    @GetMapping("/post/search/inBoard")
    public List<GetPostResponse> searchPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam("input") String input) {
        return postService.searchByKeywordAndBoard(boardId, input);
    }
}
