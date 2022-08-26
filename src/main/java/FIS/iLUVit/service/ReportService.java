package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ReportCreateRequest;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.domain.reports.CommentReport;
import FIS.iLUVit.domain.reports.PostReport;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.ReportRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 작성날짜: 2022/08/25
     * 작성자: 최민아
     * 작성내용: 신고하기
     */
    public Long registerReport(Long userId, ReportCreateRequest request) {
        if (userId == null){
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        User findTargetUser = userRepository.getById(request.getTargetUserId());

        Long reportId = null;
        // 신고 대상은 게시글(POST)와 댓글(COMMENT)
        if (request.getType().equals(ReportType.POST)){
            // 해당 게시글이 삭제되었으면 신고 불가능
            Post findPost = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

            // 해당 게시글을 이미 신고했으면 중복으로 신고 불가능
            reportRepository.findByTargetPostIdAndUserId(request.getTargetId(), userId)
                    .ifPresent(report -> {throw new ReportException(ReportErrorResult.ALREADY_EXIST_POST_REPORT);});

            // 신고 가능
            PostReport postReport = new PostReport(findPost, findUser, findTargetUser, request.getReason());
            reportId = reportRepository.save(postReport).getId();
        }else if (request.getType().equals(ReportType.COMMENT)){
            // 해당 댓글이 삭제되었으면 신고 불가능
            Comment findComment = commentRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));

            // 해당 댓글을 이미 신고했으면 중복으로 신고 불가능
            reportRepository.findByTargetCommentIdAndUserId(request.getTargetId(), userId)
                    .ifPresent(report -> {throw new ReportException(ReportErrorResult.ALREADY_EXIST_COMMENT_REPORT);});

            // 신고 가능
            CommentReport commentReport = new CommentReport(findComment, findUser, findTargetUser, request.getReason());
            reportId = reportRepository.save(commentReport).getId();
        }

        return reportId;
    }
}
