package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.dto.report.ReportRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.domain.reports.Report;
import FIS.iLUVit.domain.reports.ReportDetail;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService target;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ReportDetailRepository reportDetailRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;

    ReportRequest reportRequest;
    Center center;
    Board board;
    Teacher targetUser, user;
    Post post;
    Comment comment;
    Report reportPost, reportComment;
    ReportDetail reportDetailPost, reportDetailComment;

    @BeforeEach
    public void init(){
        center = Creator.createCenter(1L, "center");
        board = Creator.createBoard(2L, "board", center, false);

        targetUser = Creator.createTeacher(3L);
        user = Creator.createTeacher(4L);

        post = Creator.createPost(5L, "title", "content", true, board, targetUser);
        comment = Creator.createComment(6L, false, "content", post, targetUser);

        reportPost = Creator.createReport(7L, 5L);
        reportComment = Creator.createReport(8L, 6L);

        reportDetailPost = Creator.createReportDetailPost(9L, user, post);
        reportDetailComment = Creator.createReportDetailComment(10L, user, comment);
    }

    @Test
    public void 신고_실패_유저토큰무효(){
        //given
        Long userId = null;
        //when
        UserException result = assertThrows(UserException.class,
                () -> target.registerReport(userId, reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_TOKEN);
    }

    @Test
    public void 신고_실패_유저존재안함(){
        //given
        doReturn(Optional.empty())
                .when(userRepository)
                .findById(user.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> target.registerReport(user.getId(), reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 게시글신고_실패_게시글존재안함(){
        //given
        reportRequest = new ReportRequest(post.getId(),ReportType.POST, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.empty())
                .when(postRepository)
                .findById(reportRequest.getTargetId());

        //when
        PostException result = assertThrows(PostException.class,
                () -> target.registerReport(user.getId(), reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(PostErrorResult.POST_NOT_EXIST);
    }

    @Test
    public void 게시글신고_실패_중복신고(){
        //given
        reportRequest = new ReportRequest(post.getId(),ReportType.POST, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(post))
                .when(postRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.of(reportDetailPost))
                .when(reportDetailRepository)
                .findByUserIdAndTargetPostId(user.getId(), reportRequest.getTargetId());

        //when
        ReportException result = assertThrows(ReportException.class,
                () -> target.registerReport(user.getId(), reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(ReportErrorResult.ALREADY_EXIST_POST_REPORT);
    }

    @Test
    public void 게시글신고_성공_최초신고일때(){
        //given
        reportRequest = new ReportRequest(post.getId(),ReportType.POST, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(post))
                .when(postRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.empty())
                .when(reportDetailRepository)
                .findByUserIdAndTargetPostId(user.getId(), reportRequest.getTargetId());

        // 최초 신고면 null 반환
        doReturn(Optional.empty())
                .when(reportRepository)
                .findByTargetId(reportRequest.getTargetId());

        // 최최 신고면 Report, ReportDetail 저장
        doReturn(reportPost)
                .when(reportRepository)
                .save(any());

        doReturn(reportDetailPost)
                .when(reportDetailRepository)
                .save(any());

        //when
        Long savedReportId = target.registerReport(user.getId(), reportRequest);

        //then
        assertThat(savedReportId).isEqualTo(reportDetailPost.getId());
    }

    @Test
    public void 게시글신고_성공_최초신고아닐때(){
        //given
        reportRequest = new ReportRequest(post.getId(),ReportType.POST, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(post))
                .when(postRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.empty())
                .when(reportDetailRepository)
                .findByUserIdAndTargetPostId(user.getId(), reportRequest.getTargetId());

        // 최초 신고가 아니면 report 반환
        doReturn(Optional.of(reportPost))
                .when(reportRepository)
                .findByTargetId(reportRequest.getTargetId());

        // 최초 신고가 아니면 ReportDetail만 저장
        doReturn(reportDetailPost)
                .when(reportDetailRepository)
                .save(any());

        //when
        Long savedReportId = target.registerReport(user.getId(), reportRequest);

        //then
        assertThat(savedReportId).isEqualTo(reportDetailPost.getId());
    }

    @Test
    public void 댓글신고_실패_댓글존재안함(){
        //given
        reportRequest = new ReportRequest(comment.getId(),ReportType.COMMENT, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.empty())
                .when(commentRepository)
                .findById(reportRequest.getTargetId());

        //when
        CommentException result = assertThrows(CommentException.class,
                () -> target.registerReport(user.getId(), reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(CommentErrorResult.NO_EXIST_COMMENT);
    }

    @Test
    public void 댓글신고_실패_중복신고(){
        //given
        reportRequest = new ReportRequest(comment.getId(),ReportType.COMMENT, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.of(reportDetailComment))
                .when(reportDetailRepository)
                .findByUserIdAndTargetCommentId(user.getId(), reportRequest.getTargetId());

        //when
        ReportException result = assertThrows(ReportException.class,
                () -> target.registerReport(user.getId(), reportRequest));
        //then
        assertThat(result.getErrorResult()).isEqualTo(ReportErrorResult.ALREADY_EXIST_COMMENT_REPORT);

    }

    @Test
    public void 댓글신고_성공_최초신고일때(){
        //given
        reportRequest = new ReportRequest(comment.getId(),ReportType.COMMENT, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.empty())
                .when(reportDetailRepository)
                .findByUserIdAndTargetCommentId(user.getId(), reportRequest.getTargetId());

        // 최초 신고 이면 null 반환
        doReturn(Optional.empty())
                .when(reportRepository)
                .findByTargetId(reportRequest.getTargetId());

        doReturn(reportComment)
                .when(reportRepository)
                .save(any());

        doReturn(reportDetailComment)
                .when(reportDetailRepository)
                .save(any());

        //when
        Long savedReportId = target.registerReport(user.getId(), reportRequest);

        //then
        assertThat(savedReportId).isEqualTo(reportDetailComment.getId());
    }

    @Test
    public void 댓글신고_성공_최초신고아닐때(){
        //given
        reportRequest = new ReportRequest(comment.getId(),ReportType.COMMENT, ReportReason.REPORT_A);

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(user.getId());

        doReturn(Optional.of(targetUser))
                .when(userRepository)
                .findById(targetUser.getId());

        doReturn(Optional.of(comment))
                .when(commentRepository)
                .findById(reportRequest.getTargetId());

        doReturn(Optional.empty())
                .when(reportDetailRepository)
                .findByUserIdAndTargetCommentId(user.getId(), reportRequest.getTargetId());

        // 최초 신고가 아니면 report 반환
        doReturn(Optional.of(reportComment))
                .when(reportRepository)
                .findByTargetId(reportRequest.getTargetId());

        // 최초 신고가 아니면 ReportDetail만 저장
        doReturn(reportDetailComment)
                .when(reportDetailRepository)
                .save(any());

        //when
        Long savedReportId = target.registerReport(user.getId(), reportRequest);

        //then
        assertThat(savedReportId).isEqualTo(reportDetailComment.getId()); }
}