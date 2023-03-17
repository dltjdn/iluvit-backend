package FIS.iLUVit.stub;

import FIS.iLUVit.service.MessageService;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public class MessageServiceStub implements MessageService {

    public String messageHistory = "";

    @Override
    public SingleMessageSentResponse sendOne(SingleMessageSendingRequest request) {
        messageHistory = messageHistory + request.getMessage().getTo() + request.getMessage().getText();
        return null;
    }
}
