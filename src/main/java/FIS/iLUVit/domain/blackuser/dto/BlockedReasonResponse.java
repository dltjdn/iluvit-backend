package FIS.iLUVit.domain.blackuser.dto;

import FIS.iLUVit.domain.blackuser.domain.UserStatus;
import FIS.iLUVit.domain.report.dto.ReportReasonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockedReasonResponse {
    private String nickName;        // 차단된 유저의 닉네임
    private UserStatus status;      // 차단된 유저의 상태
    private String blockedDate;     // 이용제한 혹은 영구정지를 당한 일시
    private List<ReportReasonResponse> report;

}
