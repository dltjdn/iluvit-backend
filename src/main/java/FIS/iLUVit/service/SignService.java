package FIS.iLUVit.service;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class SignService {

    private final DefaultMessageService messageService;

    @Autowired
    public SignService(@Value("${coolsms.api_key}") String api_key, @Value("${coolsms.api_secret}") String api_secret, @Value("${coolsms.domain}") String domain){
        this.messageService = NurigoApp.INSTANCE.initialize(api_key, api_secret, domain);
    }

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    public void sendAuthNumber(String toNumber) {
        Message message = new Message();
        System.out.println("fromNumber = " + fromNumber);
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[아이러빗] 인증번호 " + createRandomNumber() + " 를 입력하세요.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println("response = " + response);
    }

    private String createRandomNumber() {
        String authNumber = "";
        Random random = new Random();
        for(int i=0; i<4; i++){
            String ran = Integer.toString(random.nextInt(10));
            authNumber += ran;
        }
        return authNumber;
    }
}
