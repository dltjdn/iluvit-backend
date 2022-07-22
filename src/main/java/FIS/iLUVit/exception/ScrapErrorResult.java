package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapErrorResult implements ErrorResult {

    NOT_EXIST_SCRAP(HttpStatus.BAD_REQUEST, "존재하지 않는 스크랩폴더입니다."),
    NOT_EXIST_POST(HttpStatus.BAD_REQUEST, "존재하지 않는 post입니다."),
    NOT_VALID_SCRAP(HttpStatus.BAD_REQUEST, "잘못된 스크랩폴더입니다."),
    CANT_DELETE_DEFAULT(HttpStatus.BAD_REQUEST, "기본 스크랩폴더는 삭제할 수 없습니다."),
    NOT_VALID_POST(HttpStatus.BAD_REQUEST, "잘못된 게시물입니다."),
    NOT_VALID_SCRAPPOST(HttpStatus.BAD_REQUEST, "유효하지 않은 scrapPostId 입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
