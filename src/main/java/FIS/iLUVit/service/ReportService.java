package FIS.iLUVit.service;

import FIS.iLUVit.dto.report.ReportRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.domain.reports.Report;
import FIS.iLUVit.domain.reports.ReportDetailComment;
import FIS.iLUVit.domain.reports.ReportDetailPost;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 부적절한 게시글 혹은 댓글 신고하기
     */
    public Long registerReport(Long userId, ReportRequest request) {
        if (userId == null){
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        // 신고자 정보
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Long reportDetailId = null;

        // 신고 대상은 게시글(POST)와 댓글(COMMENT)
        if (request.getType().equals(ReportType.POST)){
            // 해당 게시글이 삭제되었으면 신고 불가능
            Post findPost = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

            // 게시글 작성자 정보
            User findTargetUser = userRepository.findById(findPost.getUser().getId())
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

            // 해당 게시글을 유저가 이미 신고했으면 중복으로 신고 불가능
            reportDetailRepository.findByUserAndReport_TargetId(findUser, request.getTargetId())
                    .ifPresent(rd -> {
                        throw new ReportException(ReportErrorResult.ALREADY_EXIST_POST_REPORT);
                    });

            // 신고 가능
            // 게시글에 대한 최초 신고일 때 : 1) Report 추가 2) ReportDetail 추가
            // 게시글에 대한 최초 신고가 아닐 때 : 1) ReportDetail 추가
            Report findReport = reportRepository.findByTargetId(request.getTargetId()).orElse(null);

            if (findReport == null){ // 최초 신고
                Report report = new Report(request.getType(), request.getTargetId(), findTargetUser);
                Report saveReport = reportRepository.save(report);

                ReportDetailPost reportDetailPost = new ReportDetailPost(saveReport, findUser, request.getReason(), findPost);
                reportDetailId = reportDetailRepository.save(reportDetailPost).getId();
                saveReport.plusCount();
            }else { // 최초 신고가 아님
                findReport.updateStatus();

                ReportDetailPost reportDetailPost = new ReportDetailPost(findReport, findUser, request.getReason(), findPost);
                reportDetailId = reportDetailRepository.save(reportDetailPost).getId();
                findReport.plusCount();
            }

        }else if (request.getType().equals(ReportType.COMMENT)){
            // 해당 댓글이 삭제되었으면 신고 불가능
            Comment findComment = commentRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));

            // 댓글 작성자 정보
            User findTargetUser = userRepository.findById(findComment.getUser().getId())
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

            // 해당 댓글을 이미 신고했으면 중복으로 신고 불가능
            reportDetailRepository.findByUserAndReport_TargetId(findUser, request.getTargetId())
                    .ifPresent(rd -> {
                        throw new ReportException(ReportErrorResult.ALREADY_EXIST_COMMENT_REPORT);
                    });

            // 신고 가능
            // 댓글에 대한 최초 신고일 때 : 1) Report 추가 2) ReportDetail 추가
            // 댓글에 대한 최초 신고가 아닐 때 : 1) ReportDetail 추가
            Report findReport = reportRepository.findByTargetId(request.getTargetId()).orElse(null);

            if (findReport == null){ // 최초 신고
                Report report = new Report(request.getType(), request.getTargetId(), findTargetUser);
                Report saveReport = reportRepository.save(report);

                ReportDetailComment reportDetailComment = new ReportDetailComment(saveReport, findUser, request.getReason(), findComment);
                reportDetailId = reportDetailRepository.save(reportDetailComment).getId();
                saveReport.plusCount();
            }else { // 최초 신고가 아님
                ReportDetailComment reportDetailComment = new ReportDetailComment(findReport, findUser, request.getReason(), findComment);
                reportDetailId = reportDetailRepository.save(reportDetailComment).getId();

                findReport.updateStatus();
                findReport.plusCount();
            }
        }

        return reportDetailId;
    }
}
