package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CommentDTO;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void registerComment(Long userId, Long postId, Long commentId, RegisterCommentRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글"));
        Comment comment = new Comment(request.getAnonymous(), request.getContent(), findPost, findUser);

        if (commentId != null) {
            commentRepository.findById(commentId)
                    .ifPresent(p -> comment.updateParentComment(p));
        }

        commentRepository.save(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        commentRepository.findById(commentId)
                .ifPresentOrElse(c -> {
                    if (c.getUser().getId() == userId) {
                        c.updateContent("삭제된 댓글입니다.");
                        c.updateUser(null);
                    } else {
                        throw new UserException("삭제 권한없는 유저");
                    }
                }, () -> {
                    throw new CommentException("존재하지 않는 댓글");
                });
    }

    public Slice<CommentDTO> searchByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUser(userId, pageable).map(CommentDTO::new);
    }
}
