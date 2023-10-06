package FIS.iLUVit.domain.board.exception;

import lombok.Getter;

@Getter
public class BoardException extends RuntimeException {

    private BoardErrorResult errorResult;

    public BoardException() {
        super();
    }

    public BoardException(String message) {
        super(message);
    }

    public BoardException(BoardErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }

}
