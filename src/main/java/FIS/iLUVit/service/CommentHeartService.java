package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.Comment;
import FIS.iLUVit.domain.iluvit.CommentHeart;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.exception.CommentErrorResult;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.repository.iluvit.CommentHeartRepository;
import FIS.iLUVit.repository.iluvit.CommentRepository;
import FIS.iLUVit.repository.iluvit.UserRepository;
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

    public Long save(Long userId, Long comment_id) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }
        User findUser = userRepository.getById(userId);
        Comment findComment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));
        commentHeartRepository.findByUserAndComment(userId, comment_id)
                .ifPresent((ch) -> {
                    throw new CommentException(CommentErrorResult.ALREADY_EXIST_HEART);
                });

        CommentHeart commentHeart = new CommentHeart(findUser, findComment);
        findComment.plusHeartCnt();
        return commentHeartRepository.save(commentHeart).getId();
    }

    public Long delete(Long userId, Long comment_id) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }
        CommentHeart commentHeart = commentHeartRepository.findByUserAndComment(userId, comment_id)
                .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT_HEART));

        if (commentHeart.getUser() == null || !Objects.equals(commentHeart.getUser().getId(), userId)) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS_HEART);
        }
        Long deletedId = commentHeart.getId();
        commentHeartRepository.delete(commentHeart);
        Comment comment = commentHeart.getComment();
        comment.minusHeartCnt();
        return deletedId;
    }
}
