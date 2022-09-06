package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorResult implements ErrorResult {

    UNAUTHORIZED_USER_ACCESS(HttpStatus.BAD_REQUEST, "리뷰 작성 혹은 좋아요 등록 권한이 없는 유저입니다."),
    NO_MORE_THAN_ONE_REVIEW(HttpStatus.BAD_REQUEST, "하나의 센터 당 하나의 리뷰만 작성할 수 있습니다."),
    NO_MORE_THAN_ONE_REVIEW_HEART(HttpStatus.BAD_REQUEST, "하나의 리뷰 당 하나의 좋아요만 등록할 수 있습니다."),
    REVIEW_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 리뷰가 존재하지 않습니다."),
    REVIEW_HEART_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 좋아요가 존재하지 않습니다."),
    APPROVAL_INCOMPLETE(HttpStatus.FORBIDDEN, "승인 대기 중이거나 승인 반려된 상태입니다."),

    ;


    private final HttpStatus httpStatus;
    private final String message;
}
