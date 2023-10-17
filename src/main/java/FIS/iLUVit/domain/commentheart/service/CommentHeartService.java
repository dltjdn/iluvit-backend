package FIS.iLUVit.domain.commentheart.service;

import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.commentheart.domain.CommentHeart;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.comment.exception.CommentErrorResult;
import FIS.iLUVit.domain.comment.exception.CommentException;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.commentheart.repository.CommentHeartRepository;
import FIS.iLUVit.domain.comment.repository.CommentRepository;
import FIS.iLUVit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentHeartService {

    private final CommentHeartRepository commentHeartRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글 좋아요 등록
     */
    public void saveCommentHeart(Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        commentHeartRepository.findByUserAndComment(user, comment)
                .ifPresent((commentHeart) -> {
                    throw new CommentException(CommentErrorResult.ALREADY_HEART_COMMENT);
                });

        comment.plusHeartCnt();
        commentHeartRepository.save(CommentHeart.of(user, comment));
    }

    /**
     * 댓글 좋아요 취소
     */
    public void deleteCommentHeart(Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        CommentHeart commentHeart = commentHeartRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CommentException(CommentErrorResult.COMMENT_HEART_NOT_FOUND));

        if (!Objects.equals(commentHeart.getUser().getId(), userId)) {
            throw new CommentException(CommentErrorResult.FORBIDDEN_ACCESS);
        }

        commentHeartRepository.delete(commentHeart);

        commentHeart.getComment().minusHeartCnt();
    }

    /**
     * 예외처리 - 존재하는 댓글인가
     */
    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorResult.COMMENT_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}
