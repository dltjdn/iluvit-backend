package FIS.iLUVit.service;

import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface MessageService {
    SingleMessageSentResponse sendOne(SingleMessageSendingRequest request);
}
