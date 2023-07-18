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
    public Long saveCommentHeart(Long userId, Long commentId) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
      ;
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));

        commentHeartRepository.findByUserAndComment(findUser, findComment)
                .ifPresent((ch) -> {
                    throw new CommentException(CommentErrorResult.ALREADY_EXIST_HEART);
                });

        CommentHeart commentHeart = new CommentHeart(findUser, findComment);

        findComment.plusHeartCnt();

        return commentHeartRepository.save(commentHeart).getId();
    }

    /**
     * 댓글 좋아요 취소
     */
    public void deleteCommentHeart(Long userId, Long commentId) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));

        CommentHeart commentHeart = commentHeartRepository.findByUserAndComment(findUser, findComment)
                .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT_HEART));

        if (commentHeart.getUser() == null || !Objects.equals(commentHeart.getUser().getId(), userId)) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }

        commentHeartRepository.delete(commentHeart);

        Comment comment = commentHeart.getComment();

        comment.minusHeartCnt();
    }
}
