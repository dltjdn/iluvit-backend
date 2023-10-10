package FIS.iLUVit.domain.authnum.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final DefaultMessageService defaultMessageService;

    @Override
    public SingleMessageSentResponse sendOne(SingleMessageSendingRequest request) {
        return defaultMessageService.sendOne(request);
    }
}
