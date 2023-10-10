package FIS.iLUVit.domain.report.dto;

import FIS.iLUVit.domain.report.domain.ReportReason;
import FIS.iLUVit.domain.report.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private Long targetId;          // 신고할 대상의 id
    private ReportType type;        // 신고할 대상의 타입
    private ReportReason reason;    // 신고 사유
}
