package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.FindPasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumException;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class SignService {

    private final DefaultMessageService messageService;
    private final UserRepository userRepository;
    private final AuthNumberRepository authNumberRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public SignService(AuthNumberRepository authNumberRepository, UserRepository userRepository, BCryptPasswordEncoder encoder,
                       @Value("${coolsms.api_key}") String api_key, @Value("${coolsms.api_secret}") String api_secret, @Value("${coolsms.domain}") String domain) {
        this.messageService = NurigoApp.INSTANCE.initialize(api_key, api_secret, domain);
        this.userRepository = userRepository;
        this.authNumberRepository = authNumberRepository;
        this.encoder = encoder;
    }

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    /**
     * 작성날짜: 2022/05/24 10:38 AM
     * 작성자: 이승범
     * 작성내용: 회원가입을 위한 인증번호 전송
     */
    public void sendAuthNumberForSignup(String toNumber, AuthKind authKind) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumException("이미 서비스에 가입된 핸드폰 번호입니다.");
        }
        sendAuthNumber(toNumber, authKind);
    }

    /**
     * 작성날짜: 2022/05/25 10:44 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디를 찾기위한 인증번호 전송
     */
    public void sendAuthNumberForFindLoginId(String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumException("서비스에 가입되지 않은 핸드폰 번호입니다.");
        }
        sendAuthNumber(toNumber, AuthKind.findLoginId);
    }

    /**
     * 작성날짜: 2022/05/25 10:55 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호를 찾기위한 인증번호 전송
     */
    public void sendAuthNumberForFindPassword(String loginId, String toNumber) {

        User findUser = userRepository.findByLoginIdAndPhoneNumber(loginId, toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumException("아이디와 휴대폰번호를 확인해주세요.");
        }
        sendAuthNumber(toNumber, AuthKind.findPwd);
    }

    /**
     * 작성날짜: 2022/05/24 10:40 AM
     * 작성자: 이승범
     * 작성내용: 인증번호 입력로직
     */
    public AuthNumber authenticateAuthNum(AuthenticateAuthNumRequest request) {

        AuthNumber authNumber =
                authNumberRepository
                        .findByPhoneNumAndAuthNumAndAuthKind(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind())
                        .orElse(null);

        if (authNumber == null) {
            throw new AuthNumException("인증번호가 일치하지 않습니다.");
        } else if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > 180) {
            throw new AuthNumException("인증번호가 만료되었습니다.");
        } else {
            authNumber.AuthComplete();
        }
        return authNumber;
    }

    /**
     * 작성날짜: 2022/05/24 10:40 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디를 찾기위해
     */
    public String findLoginId(AuthenticateAuthNumRequest request) {

        AuthNumber authNumber = authenticateAuthNum(request);

        User findUser = userRepository.findByPhoneNumber(authNumber.getPhoneNum())
                .orElseThrow(() -> new AuthNumException("핸드폰 번호를 확인해 주세요"));

        authNumberRepository.delete(authNumber);
        return blindLoginId(findUser.getLoginId());
    }

    /**
     * 작성날짜: 2022/05/25 4:14 PM
     * 작성자: 이승범
     * 작성내용: 비밀번호 찾기 근데 이제 변경을 곁들인
     */
    public void changePassword(FindPasswordRequest request) {

        if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new SignupException("비밀번호와 비밀번호확인이 서로 다릅니다.");
        }

        AuthNumber authComplete = authNumberRepository.findAuthComplete(request.getPhoneNum(), AuthKind.findPwd).orElse(null);
        if (authComplete == null) {
            throw new AuthNumException("핸드폰 인증이 완료되지 않았습니다.");
        } else if(Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 10)){
            throw new AuthNumException("핸드폰 인증시간이 만료되었습니다.");
        }

        User user = userRepository.findByLoginIdAndPhoneNumber(request.getLoginId(), request.getPhoneNum())
                .orElseThrow(() -> new UserException("잘못된 로그인 아이디입니다."));

        user.changePassword(encoder.encode(request.getNewPwd()));
        authNumberRepository.delete(authComplete);
    }

    // 인증번호 전송 로직
    private void sendAuthNumber(String toNumber, AuthKind authKind) {

        // 4자리 랜덤 숫자 생성
        String authNumber = createRandomNumber();

        AuthNumber overlaps = authNumberRepository.findOverlap(toNumber, authKind).orElse(null);

        // 인증번호 최초 요청인 경우
        if (overlaps == null) {

            // 인증번호 보내고
            requestCoolSMS(toNumber, authNumber);
            // 인증번호 관련 정보를 db에 저장
            AuthNumber authNumberInfo = new AuthNumber(toNumber, authNumber, authKind);
            authNumberRepository.save(authNumberInfo);

            // 이미 인증번호를 받았지만 제한시간이 지난 경우
        } else if (Duration.between(overlaps.getCreatedDate(), LocalDateTime.now()).getSeconds() > 180) {

            // 예전 인증번호 관련 정보를 db에서 지우고
            authNumberRepository.deleteExpiredNumber(toNumber, authKind);
            // 인증번호 보낸 후
            requestCoolSMS(toNumber, authNumber);
            // 인증번호 관련 정보를 db에 저장
            AuthNumber authNumberInfo = new AuthNumber(toNumber, authNumber, authKind);
            authNumberRepository.save(authNumberInfo);

            // 이미 인증번호를 요청하였고 제한시간이 지나지 않은 경우
        } else {
            throw new AuthNumException("해당 번호로 인증 진행중입니다. 인증번호를 분실하였다면 3분 후 다시 시도해주세요");
        }
    }

    // 로그인 찾기에서 로그인아이디 일부를 *로 가리기
    private String blindLoginId(String loginId) {
        StringBuilder builder = new StringBuilder(loginId);
        int mid = loginId.length() / 2;
        builder.setCharAt(mid, '*');
        builder.setCharAt(mid - 1, '*');
        builder.setCharAt(mid + 1, '*');
        return builder.toString();
    }

    // CoolSMS 문자전송 요청
    private void requestCoolSMS(String toNumber, String authNumber) {

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[아이러빗] 인증번호 " + authNumber + " 를 입력하세요.");

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    // 4자리 랜던 숫자 생성
    private String createRandomNumber() {
        String authNumber = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(random.nextInt(10));
            authNumber += ran;
        }
        return authNumber;
    }
}
