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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlackUserService {
    private final BlackUserRepository blackUserRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final UserRepository userRepository;

    private static final int THRESHOLD_FOR_RESTRICTION = 3; // 이용제한 신고 누적 수 한계
    private static final int THRESHOLD_FOR_SUSPENDED = 7;   // 영구정지 신고 누적 수 한계

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
        BlockedReasonResponse response = new BlockedReasonResponse(blackUser.getNickName(), blackUser.getUserStatus(), formattedBlackUserDate, reasonResponses);

        return response;
    }

    /**
     * 신고 누적으로 인한 블랙유저를 등록합니다
     */
    @Scheduled(cron = "0 0 */4 * * *") // 4시간 마다 실행
    public void makeBlackUserUponReportThreshold() {
        // 사용자별 신고 횟수 정보 조회
        List<Object[]> userReportCounts = reportRepository.countReportsByUserAndStatus(ReportStatus.DELETE);

        // 각 사용자별로 신고 횟수 확인 후 누적횟수에 따른 블랙유저로 등록
        for (Object[] userReportRow : userReportCounts) {
            User user = (User) userReportRow[0];
            Long reportCount = (Long) userReportRow[1];
            int reportCountInt = reportCount.intValue();
            if(user.getPhoneNumber() != null) {
                if (reportCountInt >= THRESHOLD_FOR_RESTRICTION && reportCountInt <= THRESHOLD_FOR_SUSPENDED) {
                    // 신고 누적 횟수가 이용제한 한계에 도달한 경우, 사용자를 이용제한 상태로 변경
                    processBlacklistUser(user, UserStatus.RESTRICTED_SEVEN_DAYS);
                } else if (reportCountInt >= THRESHOLD_FOR_SUSPENDED) {
                    // 신고 누적 횟수가 영구정지 한계에 도달한 경우, 사용자를 영구정지 상태로 변경
                    processBlacklistUser(user, UserStatus.BAN);
                }
            }
        }
    }

    /**
     * 해당 유저를 블랙유저로 추가하고 개인정보를 삭제합니다
     */
    private void processBlacklistUser(User user, UserStatus status) {
        // 블랙리스트에 사용자를 추가
        blackUserRepository.save(new BlackUser(user, status));
        // 사용자의 개인 정보를 삭제
        user.deletePersonalInfo();
    }

    /**
     * 블랙 유저인지 검증합니다
     */
    public void isValidUser(String phoneNum) {
        //현재 영구정지, 일주일간 이용제한에 대한 이용제한 유저는 가입 불가
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

    /**
     * 블랙 유저 디비를 매일 자정마다 정리합니다
     */
    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void deleteOldData() {
        // 15일 뒤 WITHDRAWN인 blackUser 삭제
        LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);

        List<BlackUser> blackUsersByWithDrawn = blackUserRepository.findByCreatedDateBeforeAndUserStatus(fifteenDaysAgo, UserStatus.WITHDRAWN);

        blackUserRepository.deleteAll(blackUsersByWithDrawn);

        // 7일 뒤 RESTRICTED_REPORT인 blackUser 삭제 및 User로 복귀
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<BlackUser> blackUsersByRestriectdReport = blackUserRepository.findByCreatedDateBeforeAndUserStatus(sevenDaysAgo, UserStatus.RESTRICTED_SEVEN_DAYS);

        blackUsersByRestriectdReport.forEach(blackUser -> {
            User user = userRepository.findById(blackUser.getUserId())
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
            user.restorePersonalInfo(blackUser);
        });

        blackUserRepository.deleteAll(blackUsersByRestriectdReport);


    }

}