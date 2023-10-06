package FIS.iLUVit.domain.scrap.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 스크랩이 존재하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    NOT_VALID_SCRAP(HttpStatus.BAD_REQUEST, "잘못된 스크랩폴더입니다."),
    CANNOT_DELETE_DEFAULT(HttpStatus.BAD_REQUEST, "기본 스크랩폴더는 삭제할 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
