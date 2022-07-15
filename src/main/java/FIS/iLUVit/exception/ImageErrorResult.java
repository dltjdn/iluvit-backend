package FIS.iLUVit.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ImageErrorResult {

    IMAGE_ANALYZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 경로 분석 중 서버오류 발생"),
    IMAGE_DIRECTORY_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 폴더 생성 중 서버오류 발생"),
    IMAGE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 도중 오류 발생")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
