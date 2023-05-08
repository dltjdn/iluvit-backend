package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.comment.CommentDto;
import FIS.iLUVit.dto.comment.CommentRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.PostAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;

    private final AlarmRepository alarmRepository;

    public Long registerComment(Long userId, Long postId, Long p_commentId, CommentRequest request) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Post findPost = postRepository.findByIdWithBoard(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));


        // anonymous false 일 때 order = null
        // anonymous true 일 때 order = n
        Integer anonymousOrder = null;

        /**
            작성자: 이창윤
            작성시간: 2022/08/02 11:13 AM
            내용: 익명3이 댓글을 또 달면 3을 가져와야됨.
                새로운 유저가 익명으로 댓글을 달면 익명4로 등록함.
                작성자가 익명으로 작성하면 익명(작성자)로 표시됨.
                닉네임 공개로 작성할 경우 order = null
        */
        // 익명 작성일 때
        if (request.getAnonymous()) {
            // 게시글 작성자 == 댓글 작성자이면 -1
            if (Objects.equals(findPost.getUser().getId(), userId)) {
                anonymousOrder = -1;
            } else {
                Comment findComment = commentRepository.findFirstByPostAndUserAndAnonymous(findPost, findUser, request.getAnonymous())
                    .orElse(null);
                // 게시글 내 이미 익명으로 작성한 댓글이 있으면 댓글에서 익명 순서 가져옴
                if (findComment != null) {
                    anonymousOrder = findComment.getAnonymousOrder();
                } else { // 없으면 게시글 order +1 후 익명 순서 새로 가져옴
                    findPost.plusAnonymousOrder();
                    anonymousOrder = findPost.getAnonymousOrder();
                }
            }
        }

        Comment comment = new Comment(request.getAnonymous(), request.getContent(), findPost, findUser, anonymousOrder);
        if (p_commentId != null) {
            Comment parentComment = commentRepository.getById(p_commentId);
            comment.updateParentComment(parentComment);
        }
        if (!userId.equals(findPost.getUser().getId())) { // 본인 게시글에 댓글단 건 알림 X
            Alarm alarm = new PostAlarm(findPost.getUser(), findPost, comment);
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);
        }
        return commentRepository.save(comment).getId();
    }

    public Long deleteComment(Long userId, Long commentId) {
        if (userId == null) {
            throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        commentRepository.findById(commentId)
                .ifPresentOrElse(c -> {
                    // 내용 -> 삭제된 댓글입니다. + 작성자 -> null
                    if (Objects.equals(c.getUser().getId(), userId)) {
                        //c.deleteComment();

                        // 댓글과 연관된 모든 신고내역의 target_id 를 null 값으로 만들어줘야함.
                        reportRepository.setTargetIsNullAndStatusIsDelete(c.getId());
                        // 댓글과 연관된 모든 신고상세내역의 target_comment_id(fk) 를 null 값으로 만들어줘야함.
                        List<Long> commentIds = List.of(c.getId());
                        reportDetailRepository.setCommentIsNull(commentIds);

                        Comment findComment = commentRepository.findById(commentId).orElse(null);
                        findComment.deleteComment();
                    } else {
                        throw new CommentException(CommentErrorResult.UNAUTHORIZED_USER_ACCESS);
                    }
                }, () -> {
                    throw new CommentException(CommentErrorResult.NO_EXIST_COMMENT);
                });
        return commentId;
    }

    public Slice<CommentDto> searchByUser(Long userId, Pageable pageable) {
        // Comment -> CommentDTO 타입으로 변환
        return commentRepository.findByUser(userId, pageable).map(CommentDto::new);
    }
}
