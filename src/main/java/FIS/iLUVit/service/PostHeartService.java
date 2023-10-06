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
    public void savePostHeart(Long userId, Long postId) {
        User user = getUser(userId);

        Post post = getPost(postId);

        // 이미 좋아요 누른 게시물이면 에러
        postHeartRepository.findByUserAndPost(user, post)
                .ifPresent((postHeart) -> {
                    throw new PostException(PostErrorResult.ALREADY_HEART_POST);
                });

        postHeartRepository.save(new PostHeart(user, post));
        post.plusHeartCount(); // 좋아요 수 +1
    }


    /**
     * 게시글 좋아요 취소 ( 기존에 좋아요 눌렀던 상태여야 취소 가능 )
     */
    public void deletePostHeart(Long userId, Long postId){
        User user = getUser(userId);

        Post post = getPost(postId);

        PostHeart postHeart = postHeartRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_HEART_NOT_FOUND));

        postHeartRepository.delete(postHeart);
    }

    /**
     * 예외처리 - 존재하는 게시글인가
     */
    private Post getPost(Long postId) {
        return  postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

}
