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

    public void save(Long userId, Long comment_id) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Comment findComment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new CommentException("존재하지 않는 댓글"));

        CommentHeart commentHeart = new CommentHeart(findUser, findComment);
        commentHeartRepository.save(commentHeart);
    }

    public void delete(Long userId, Long comment_id) {
        commentHeartRepository.findById(comment_id)
                .ifPresentOrElse(ch -> {
                    if (ch.getUser().getId() == userId) {
                        commentHeartRepository.deleteById(comment_id);
                    } else {
                        throw new UserException("취소 권한 없는 유저");
                    }
                }, () -> {
                    throw new CommentException("존재하지 않는 좋아요");
                });
    }
}
