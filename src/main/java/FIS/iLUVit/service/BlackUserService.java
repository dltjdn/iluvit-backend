package FIS.iLUVit.service;

import FIS.iLUVit.domain.BlackUser;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportStatus;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.domain.reports.Report;
import FIS.iLUVit.domain.reports.ReportDetail;
import FIS.iLUVit.dto.blackUser.BlockedReasonResponse;
import FIS.iLUVit.dto.report.ReportReasonResponse;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.BlackUserRepository;
import FIS.iLUVit.repository.ReportDetailRepository;
import FIS.iLUVit.repository.ReportRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BlackUserService {

    private final BlackUserRepository blackUserRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;

    /**
     * 차단 정보를 조회합니다
     */
    public BlockedReasonResponse getBlockedReason(Long blackUserId) {

        BlackUser blackUser = blackUserRepository.findById(blackUserId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Long userId = blackUser.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Report> reports = reportRepository.findByTargetUserAndStatus(user, ReportStatus.DELETE);
        List<ReportReasonResponse> reasonResponses = new ArrayList<>();

        for(Report report : reports) {
            ReportType reportType = report.getType();
            LocalDateTime reportDate = report.getCreatedDate();
            ReportDetail reportDetail = reportDetailRepository.findByReportId(report.getId());
            ReportReason reportReason = reportDetail.getReason();

            ReportReasonResponse reasonResponse = new ReportReasonResponse(reportType, reportDate, reportReason);
            reasonResponses.add(reasonResponse);
        }
        BlockedReasonResponse response = new BlockedReasonResponse(blackUser.getUserStatus(), blackUser.getCreatedDate(), reasonResponses);

        return response;
    }
}