package FIS.iLUVit.domain.chat.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 채팅방이 존재하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),

    /**
     * 400 BAD REQUEST
     */
    CANNOT_SEND_TO_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 쪽지를 전송할 수 없습니다."),
    WITHDRAWN_USER(HttpStatus.BAD_REQUEST, "탈퇴한 유저입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
