package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.GetPostResponse;
import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.controller.dto.PostRegisterRequest;
import FIS.iLUVit.controller.dto.PostSearchRequestDTO;
import FIS.iLUVit.domain.enumtype.Auth;
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
    public List<GetPostResponsePreview> searchPost(@Login Long userId,
                                                   @RequestParam("input") String input,
                                                   @RequestParam("auth") Auth auth) {
        return postService.searchByKeyword(input, auth, userId);
    }

    @GetMapping("/post/search/inCenter")
    public List<GetPostResponsePreview> searchPostByCenter(
            @Login Long userId,
            @ModelAttribute PostSearchRequestDTO requestDTO) {
        return postService.searchByKeywordAndCenter(requestDTO.getCenterId(), requestDTO.getInput()
                , requestDTO.getAuth(), userId);
    }

    @GetMapping("/post/search/inBoard")
    public List<GetPostResponsePreview> searchPostByBoard(
            @RequestParam("board_id") Long boardId,
            @RequestParam("input") String input) {
        return postService.searchByKeywordAndBoard(boardId, input);
    }
}
