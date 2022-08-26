package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportType;
import lombok.Data;

@Data
public class ReportCreateRequest {
    private Long targetId;
    private Long targetUserId;
    private ReportType type;
    private ReportReason reason;
}
