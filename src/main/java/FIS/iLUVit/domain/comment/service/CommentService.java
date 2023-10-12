package FIS.iLUVit.domain.comment.service;

import FIS.iLUVit.domain.alarm.repository.AlarmRepository;
import FIS.iLUVit.domain.blocked.domain.Blocked;
import FIS.iLUVit.domain.blocked.repository.BlockedRepository;
import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.comment.exception.CommentErrorResult;
import FIS.iLUVit.domain.comment.exception.CommentException;
import FIS.iLUVit.domain.comment.repository.CommentRepository;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.post.exception.PostErrorResult;
import FIS.iLUVit.domain.post.exception.PostException;
import FIS.iLUVit.domain.post.repository.PostRepository;
import FIS.iLUVit.domain.report.repository.ReportDetailRepository;
import FIS.iLUVit.domain.report.repository.ReportRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.alarm.domain.Alarm;
import FIS.iLUVit.domain.alarm.domain.PostAlarm;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.common.domain.NotificationTitle;
import FIS.iLUVit.domain.comment.dto.CommentCreateRequest;
import FIS.iLUVit.domain.comment.dto.CommentPostResponse;
import FIS.iLUVit.domain.alarm.AlarmUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final BlockedRepository blockedRepository;

    /**
     * 댓글 작성 (comment_id 값이 null일 경우 댓글 작성, comment_id 값까지 보내는 경우 대댓글 작성)
     */
    public void saveNewComment(Long userId, Long postId, Long parentCommentId, CommentCreateRequest request) {
        User user = getUser(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        // anonymous false 일 때 order = null
        // anonymous true 일 때 order = n
        Integer anonymousOrder = null;

        /**
         익명3이 댓글을 또 달면 3을 가져와야됨.
         새로운 유저가 익명으로 댓글을 달면 익명4로 등록함.
         작성자가 익명으로 작성하면 익명(작성자)로 표시됨.
         닉네임 공개로 작성할 경우 order = null
        */
        // 익명 작성일 때
        if (request.getAnonymous()) {
            // 게시글 작성자 == 댓글 작성자이면 -1
            if (Objects.equals(post.getUser().getId(), userId)) {
                anonymousOrder = -1;
            } else {
                Comment findComment = commentRepository.findFirstByPostAndUserAndAnonymous(post, user, request.getAnonymous())
                    .orElse(null);
                // 게시글 내 이미 익명으로 작성한 댓글이 있으면 댓글에서 익명 순서 가져옴
                if (findComment != null) {
                    anonymousOrder = findComment.getAnonymousOrder();
                } else { // 없으면 게시글 order +1 후 익명 순서 새로 가져옴
                    post.plusAnonymousOrder();
                    anonymousOrder = post.getAnonymousOrder();
                }
            }
        }

        Comment comment = new Comment(request.getAnonymous(), request.getContent(), post, user, anonymousOrder);

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.getById(parentCommentId);
            comment.updateParentComment(parentComment);
        }

        // 게시글을 쓴 유저가 차단한 유저를 조회한다
        List<User> blockedUsers = blockedRepository.findByBlockingUser(post.getUser()).stream()
                .map(Blocked::getBlockedUser)
                .collect(Collectors.toList());

        if (!user.equals(post.getUser()) && !blockedUsers.contains(user)) { // 본인 게시글에 댓글단 건 알림 X
            Alarm alarm = new PostAlarm(post.getUser(), post, comment);
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
        }
        commentRepository.save(comment);
    }


    /**
     * 댓글 삭제 ( 댓글 데이터 지우지 않고 내용, 작성자만 null로 변경)
     */
    public void deleteComment(Long userId, Long commentId) {

        commentRepository.findById(commentId)
                .ifPresentOrElse(c -> {
                    // 내용 -> 삭제된 댓글입니다. + 작성자 -> null
                    if (!Objects.equals(c.getUser().getId(), userId)) {
                        throw new CommentException(CommentErrorResult.FORBIDDEN_ACCESS);
                    }
                    //c.deleteComment();

                    // 댓글과 연관된 모든 신고내역의 target_id 를 null 값으로 만들어줘야함.
                    reportRepository.setTargetIsNullAndStatusIsDelete(c.getId());
                    // 댓글과 연관된 모든 신고상세내역의 target_comment_id(fk) 를 null 값으로 만들어줘야함.
                    List<Long> commentIds = List.of(c.getId());
                    reportDetailRepository.setCommentIsNull(commentIds);

                    Comment findComment = commentRepository.findById(commentId).orElse(null);
                    findComment.deleteComment();
                }, () -> {
                    throw new CommentException(CommentErrorResult.COMMENT_NOT_FOUND);
                });
    }

    /**
     * 댓글 단 글 전체 조회
     */
    public Slice<CommentPostResponse> findCommentByUser(Long userId, Pageable pageable) {
        User user = getUser(userId);
        // Comment -> CommentDTO 타입으로 변환
        return commentRepository.findByUser(user, pageable).map(CommentPostResponse::new);
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
       return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }
}