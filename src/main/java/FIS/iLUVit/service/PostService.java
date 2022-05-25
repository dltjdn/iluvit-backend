package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.GetPostResponse;
import FIS.iLUVit.controller.dto.PostRegisterRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.BoardRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final BoardRepository boardRepository;

    public void savePost(PostRegisterRequest request, List<MultipartFile> images, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
        Integer imgSize = (images == null ? 0 : images.size());

        Board findBoard = boardRepository.findById(request.getBoard_id())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 보드"));

        Post post = new Post(request.getTitle(), request.getContent(), request.getAnonymous(),
                0, 0, imgSize, 0, findBoard, findUser);

        Post savedPost = postRepository.save(post); // 게시글 저장 -> Id 생김

        if (imgSize > 0) {
            String imagePath = imageService.getPostDir(savedPost.getId()); // id로 경로얻어서 이미지 저장
            imageService.saveInfoImage(images, imagePath);
        }

    }

    public void deleteById(Long postId, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글"));
        if (!Objects.equals(findPost.getUser().getId(), findUser.getId())) {
            throw new IllegalStateException("삭제 권한이 없는 유저");
        }
        postRepository.deleteById(postId);
    }


    public GetPostResponse findById(Long postId) {

        Post findPost = postRepository.findByIdWithUserAndBoardAndCenter(postId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글"));
        return getPostResponseDto(findPost);
    }

    public List<GetPostResponse> searchByKeyword(String input) {
        log.info(input);
        List<Post> posts;
        if (input.isEmpty() || input == null) {
            posts = postRepository.findAll();
        } else {
            posts = postRepository.findByKeyword(input);
        }
        return posts.stream().map(p -> getPostResponseDto(p))
                .collect(Collectors.toList());
    }

    public List<GetPostResponse> searchByKeywordAndCenter(Long centerId, String input) {
        List<Post> posts = postRepository.findByKeywordAndCenter(centerId, input);
        return posts.stream().map(p -> getPostResponseDto(p))
                .collect(Collectors.toList());
    }

    public List<GetPostResponse> searchByKeywordAndBoard(Long boardId, String input) {
        List<Post> posts = postRepository.findByKeywordAndBoard(boardId, input);
        return posts.stream().map(p -> getPostResponseDto(p))
                .collect(Collectors.toList());
    }

    private GetPostResponse getPostResponseDto(Post post) {
        String postDir = imageService.getPostDir(post.getId());
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(postDir, post.getImgCnt());
        String userProfileDir = imageService.getUserProfileDir();
        String encodedProfileImage = imageService.getEncodedProfileImage(userProfileDir, post.getUser().getId());
        return new GetPostResponse(post, encodedInfoImage, encodedProfileImage);
    }
}
