package FIS.iLUVit.service;

import FIS.iLUVit.domain.BlackUser;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportStatus;
import FIS.iLUVit.domain.enumtype.UserStatus;
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

import java.time.format.DateTimeFormatter;
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
    public BlockedReasonResponse getBlockedReason(String loginId) {

        BlackUser blackUser = blackUserRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BlackUserException(BlackUserResult.BLACK_USER_NOT_EXIST));
        Long userId = blackUser.getUserId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Report> reports = reportRepository.findByTargetUserIdAndStatus(userId, ReportStatus.DELETE);
        List<ReportReasonResponse> reasonResponses = new ArrayList<>();

        for(Report report : reports) {
            String formattedReportDate = report.getCreatedDate().format(formatter);
            ReportDetail reportDetail = reportDetailRepository.findByReportId(report.getId());
            ReportReason reportReason = reportDetail.getReason();
            ReportReasonResponse reasonResponse = new ReportReasonResponse(report.getType(), formattedReportDate, reportReason);
            reasonResponses.add(reasonResponse);
        }
        String formattedBlackUserDate = blackUser.getCreatedDate().format(formatter);
        BlockedReasonResponse response = new BlockedReasonResponse(blackUser.getUserStatus(), formattedBlackUserDate, reasonResponses);

        return response;
    }

    public void isValidUser(String phoneNum) {
        //현재 영구정지, 관리자에 의한 이용제한, 신고 누적 3회에 대한 이용제한 유저는 가입 불가
        blackUserRepository.findRestrictedByPhoneNumber(phoneNum)
                .ifPresent(blackUser -> {
                    throw new UserException(UserErrorResult.USER_IS_BLACK);
                });

        // 탈퇴 후 15일이 지나지 않은 유저는 가입 불가
        blackUserRepository.findByPhoneNumberAndUserStatus(phoneNum, UserStatus.WITHDRAWN)
                .ifPresent(blackUser -> {
                    throw new UserException(UserErrorResult.USER_IS_WITHDRAWN);
                });
    }
}