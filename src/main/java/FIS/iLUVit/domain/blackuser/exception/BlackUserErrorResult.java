package FIS.iLUVit.domain.blackuser.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlackUserErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    BLACK_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 로그인아이디를 가진 블랙유저가 존재하지 않습니다."),

    /**
     * 클라이언트와 약속된 에러코드
     */
    USER_IS_BLACK(HttpStatus.EXPECTATION_FAILED, "영구 정지 또는 이용 제한 유저입니다."),
    USER_IS_BLACK_OR_WITHDRAWN(HttpStatus.EXPECTATION_FAILED, "차단 됐거나 탈퇴 후 15일이 지나지 않은 유저입니다."),
    USER_IS_WITHDRAWN(HttpStatus.PRECONDITION_FAILED, "탈퇴 후 15일이 지나지 않은 유저입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}