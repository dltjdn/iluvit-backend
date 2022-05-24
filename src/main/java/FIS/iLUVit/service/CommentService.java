package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void registerComment(Long userId, Long postId, RegisterCommentRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저"));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글"));
        Comment comment = new Comment(request.getAnonymous(), request.getContent(), findPost, findUser);
        commentRepository.save(comment);
    }
}
