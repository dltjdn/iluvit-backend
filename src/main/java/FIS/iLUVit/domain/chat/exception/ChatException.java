package FIS.iLUVit.domain.chat.exception;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private ChatErrorResult errorResult;

    public ChatException() {
        super();
    }

    public ChatException(String message) {
        super(message);
    }

    public ChatException(ChatErrorResult errorResult) {
        super();
        this.errorResult = errorResult;
    }
}
