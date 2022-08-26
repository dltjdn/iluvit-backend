package FIS.iLUVit.domain.enumtype;

public enum ReportStatus {
    ACCEPT,    // 신고가 접수된 상태
    DELETE,     // 접수된 신고의 대상(게시글,댓글)을 삭제한 상태
    HOLD,       // 접수된 신고의 대상(게시글,댓글)의 처리 여부를 보류한 상태
    NOACTION    // 접수된 신고가 이상 없다고 판단해 신고를 반려한 상태
}
