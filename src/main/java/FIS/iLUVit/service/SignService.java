package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.domain.AuthNumberInfo;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.AuthNumException;
import FIS.iLUVit.repository.AuthNumberInfoRepository;
import FIS.iLUVit.repository.UserRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class SignService {

    private final DefaultMessageService messageService;
    private final UserRepository userRepository;
    private final AuthNumberInfoRepository authNumberInfoRepository;

    @Autowired
    public SignService(AuthNumberInfoRepository authNumberInfoRepository, UserRepository userRepository,
                       @Value("${coolsms.api_key}") String api_key, @Value("${coolsms.api_secret}") String api_secret, @Value("${coolsms.domain}") String domain){
        this.messageService = NurigoApp.INSTANCE.initialize(api_key, api_secret, domain);
        this.userRepository = userRepository;
        this.authNumberInfoRepository = authNumberInfoRepository;
    }

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    /**
    *   작성날짜: 2022/05/24 10:38 AM
    *   작성자: 이승범
    *   작성내용: 인증번호 전송 로직
    */
    public void sendAuthNumber(String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumException("이미 서비스에 가입된 핸드폰 번호입니다.");
        }

        String authNumber = createRandomNumber();

        List<AuthNumberInfo> overlaps = authNumberInfoRepository.findOverlap(toNumber);

        // 인증번호 최초 요청인 경우
        if(overlaps.isEmpty()){

            // 인증번호 보내고
            requestCoolSMS(toNumber, authNumber);
            // 인증번호 관련 정보를 db에 저장
            AuthNumberInfo authNumberInfo = new AuthNumberInfo(toNumber, authNumber);
            authNumberInfoRepository.save(authNumberInfo);

            // 이미 인증번호를 받았지만 제한시간이 지난 경우
        } else if(Duration.between(overlaps.get(0).getCreatedDate(), LocalDateTime.now()).getSeconds()>180){

            // 예전 인증번호 관련 정보를 db에서 지우고
            authNumberInfoRepository.deleteExpiredNumber(toNumber);
            // 인증번호 보낸 후
            requestCoolSMS(toNumber, authNumber);
            // 인증번호 관련 정보를 db에 저장
            AuthNumberInfo authNumberInfo = new AuthNumberInfo(toNumber, authNumber);
            authNumberInfoRepository.save(authNumberInfo);

            // 이미 인증번호를 요청하였고 제한시간이 지나지 않은 경우
        } else{
            throw new AuthNumException("해당 번호로 인증 진행중입니다. 인증번호를 분실하였다면 3분 후 다시 시도해주세요");
        }
    }

    /**
    *   작성날짜: 2022/05/24 10:40 AM
    *   작성자: 이승범
    *   작성내용: 인증번호 입력로직
    */
    public void authenticateAuthNum(AuthenticateAuthNumRequest request) {

        System.out.println("request = " + request.getAuthNum());
        System.out.println("request = " + request.getPhoneNum());

        AuthNumberInfo authNumberInfo = authNumberInfoRepository.findByPhoneNumAndAuthNum(request.getPhoneNum(), request.getAuthNum()).orElse(null);

        if (authNumberInfo == null) {
            throw new AuthNumException("인증번호가 일치하지 않습니다.");
        }
        else if(Duration.between(authNumberInfo.getCreatedDate(), LocalDateTime.now()).getSeconds()>180) {
            throw new AuthNumException("인증번호가 만료되었습니다.");
        }
        else{
            authNumberInfo.AuthComplete();
        }
    }

    // CoolSMS 문자전송 요청
    private void requestCoolSMS(String toNumber, String authNumber) {

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[아이러빗] 인증번호 " + authNumber + " 를 입력하세요.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println("response = " + response);
    }

    // 4자리 랜던 숫자 생성
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
