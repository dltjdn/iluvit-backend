package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CommentDTO;
import FIS.iLUVit.controller.dto.RegisterCommentRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.PostAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Long registerComment(Long userId, Long postId, Long commentId, RegisterCommentRequest request) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException());
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        Comment comment = new Comment(request.getAnonymous(), request.getContent(), findPost, findUser);

        // commentId 보내는 경우 대댓글 -> parentComment
        if (commentId != null) {
            Comment parentComment = commentRepository.getById(commentId);
            comment.updateParentComment(parentComment);
        }

        AlarmUtils.publishAlarmEvent(new PostAlarm(findPost.getUser(), findPost, comment));
        return commentRepository.save(comment).getId();
    }

    public Long deleteComment(Long userId, Long commentId) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        commentRepository.findById(commentId)
                .ifPresentOrElse(c -> {
                    log.info("댓글 작성자 아이디 = {}, 접속 중인 유저 아이디 = {}", c.getUser().getId(), userId);
                    // 내용 -> 삭제된 댓글입니다. + 작성자 -> null
                    if (Objects.equals(c.getUser().getId(), userId)) {
                        c.updateContent("삭제된 댓글입니다.");
                        c.updateUser(null);
                    } else {
                        throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
                    }
                }, () -> {
                    throw new CommentException(CommentErrorResult.NO_EXIST_COMMENT);
                });
        return commentId;
    }

    public Slice<CommentDTO> searchByUser(Long userId, Pageable pageable) {
        // Comment -> CommentDTO 타입으로 변환
        return commentRepository.findByUser(userId, pageable).map(CommentDTO::new);
    }
}
