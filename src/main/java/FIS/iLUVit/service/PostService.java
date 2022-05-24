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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final BoardRepository boardRepository;

    public void savePost(PostRegisterRequest request, List<MultipartFile> images, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
        Post post = new Post(request.getTitle(), request.getContent(), request.getAnonymous(),
                0, 0, images.size(), findUser);
        if (request.getBoard_id() != null) {
            Board findBoard = boardRepository.findById(request.getBoard_id())
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 보드"));
            post.updateBoard(findBoard);
        }

        Post savedPost = postRepository.save(post);

        String imagePath = imageService.getPostDir(savedPost.getId());
        imageService.saveInfoImage(images, imagePath);

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
        String postPath = imageService.getPostDir(postId);
        List<String> encodedImages = imageService.getEncodedInfoImage(postPath, findPost.getImgCnt());

        String profilePath = imageService.getUserProfileDir();
        String encodedProfileImage = imageService.getEncodedProfileImage(profilePath, findPost.getUser().getId());
        return new GetPostResponse(findPost, encodedImages, encodedProfileImage);
    }
}
