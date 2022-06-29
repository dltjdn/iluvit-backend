package FIS.iLUVit.service;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.CommentHeart;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CommentHeartRepository;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentHeartService {

    private final CommentHeartRepository commentHeartRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public Long save(Long userId, Long comment_id) {
        User findUser = userRepository.getById(userId);
        Comment findComment = commentRepository.getById(comment_id);

        CommentHeart commentHeart = new CommentHeart(findUser, findComment);
        return commentHeartRepository.save(commentHeart).getId();
    }

    public Long delete(Long userId, Long comment_id) {
        CommentHeart commentHeart = commentHeartRepository.findByUserAndComment(userId, comment_id)
                .orElseThrow(() -> new CommentException("존재하지 않는 좋아요"));
        return commentHeart.getId();
    }
}
