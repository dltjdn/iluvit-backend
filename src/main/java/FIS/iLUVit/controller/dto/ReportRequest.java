package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportType;
import lombok.Data;

@Data
public class ReportRequest {
    private Long targetId;          // 신고할 대상의 id
    private ReportType type;        // 신고할 대상의 타입
    private ReportReason reason;    // 신고 사유
}
