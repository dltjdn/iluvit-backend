package FIS.iLUVit.dto.blackUser;

import FIS.iLUVit.domain.enumtype.UserStatus;
import FIS.iLUVit.dto.report.ReportReasonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BlockedReasonResponse {
    private UserStatus status;      // 차단된 유저의 상태
    private String blockedDate;     // 이용제한 혹은 영구정지를 당한 일시
    private List<ReportReasonResponse> report;

}
