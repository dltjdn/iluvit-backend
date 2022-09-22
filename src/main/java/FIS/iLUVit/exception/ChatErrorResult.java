package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorResult implements ErrorResult {

    USER_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 유저가 존재하지 않습니다."),
    UNAUTHORIZED_USER_ACCESS(HttpStatus.FORBIDDEN, "채팅 생성 권한이 없는 유저입니다."),
    NO_SEND_TO_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 쪽지를 전송할 수 없습니다."),
    POST_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 게시글이 존재하지 않습니다."),
    ROOM_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 채팅방이 존재하지 않습니다."),
    WITHDRAWN_MEMBER(HttpStatus.BAD_REQUEST, "현재 유저 혹은 상대방이 탈퇴한 회원입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
