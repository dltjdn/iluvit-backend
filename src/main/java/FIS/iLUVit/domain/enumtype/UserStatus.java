package FIS.iLUVit.domain.enumtype;

public enum UserStatus {
    SUSPENDED,  // 영구정지
    WITHDRAWN,   // 회원탈퇴
    RESTRICTED_ADMIN, // 관리자에 의한 이용제한
    RESTRICTED_REPORT // 신고 누적에 의한 이용제한
}
