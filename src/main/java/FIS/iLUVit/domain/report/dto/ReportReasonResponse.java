package FIS.iLUVit.domain.report.dto;

import FIS.iLUVit.domain.report.domain.ReportReason;
import FIS.iLUVit.domain.report.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReportReasonResponse {
    private ReportType reportType;          // 신고된 항목의 유형
    private String reportDate;       // 신고 접수 일시
    private ReportReason reportReason;      // 해당 게시글 혹은 댓글의 신고 사유
}
