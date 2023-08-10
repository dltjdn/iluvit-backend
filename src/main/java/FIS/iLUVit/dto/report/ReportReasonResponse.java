package FIS.iLUVit.dto.report;

import FIS.iLUVit.domain.enumtype.ReportReason;
import FIS.iLUVit.domain.enumtype.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReportReasonResponse {
    private ReportType reportType;          // 신고된 항목의 유형
    private LocalDateTime reportDate;       // 신고 접수 일시
    private ReportReason reportReason;      // 해당 게시글 혹은 댓글의 신고 사유
}
