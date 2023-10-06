package FIS.iLUVit.service;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.CommentHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.CommentErrorResult;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CommentHeartRepository;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.UserRepository;
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
        User findUser = getUser(userId);
        Comment findComment = getComment(commentId);

        commentHeartRepository.findByUserAndComment(findUser, findComment)
                .ifPresent((ch) -> {
                    throw new CommentException(CommentErrorResult.ALREADY_HEART_COMMENT);
                });

        CommentHeart commentHeart = new CommentHeart(findUser, findComment);

        findComment.plusHeartCnt();
        commentHeartRepository.save(commentHeart);
    }

    /**
     * 댓글 좋아요 취소
     */
    public void deleteCommentHeart(Long userId, Long commentId) {

        User findUser = getUser(userId);

        Comment findComment = getComment(commentId);

        CommentHeart commentHeart = commentHeartRepository.findByUserAndComment(findUser, findComment)
                .orElseThrow(() -> new CommentException(CommentErrorResult.COMMENT_HEART_NOT_FOUND));

        if (!Objects.equals(commentHeart.getUser().getId(), userId)) {
            throw new CommentException(CommentErrorResult.FORBIDDEN_ACCESS);
        }

        commentHeartRepository.delete(commentHeart);

        Comment comment = commentHeart.getComment();

        comment.minusHeartCnt();
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
