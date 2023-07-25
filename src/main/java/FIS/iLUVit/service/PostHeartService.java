package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostHeartService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostHeartRepository postHeartRepository;

    /**
     * 게시글 좋아요 등록
     */
    public Long savePostHeart(Long userId, Long postId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        postHeartRepository.findByPostAndUser(userId, postId)
                .ifPresent((ph) -> {
                    throw new PostException(PostErrorResult.ALREADY_EXIST_HEART);
                });

        User findUser = userRepository.getById(userId);
        PostHeart postHeart = new PostHeart(findUser, findPost);
        return postHeartRepository.save(postHeart).getId();
    }

    /**
     * 게시글 좋아요 취소 ( 기존에 좋아요 눌렀던 상태여야 취소 가능 )
     */
    public void deletePostHeart(Long userId, Long postId){
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }
        PostHeart postHeart = postHeartRepository.findByPostAndUser(userId, postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));
        postHeartRepository.delete(postHeart);
    }

}
