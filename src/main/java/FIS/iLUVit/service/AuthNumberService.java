package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.FindPasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.messageService.MessageService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthNumberService {

    private final MessageService defaultMessageService;
    private final UserRepository userRepository;
    private final AuthNumberRepository authNumberRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    /**
     * 작성날짜: 2022/05/24 10:38 AM
     * 작성자: 이승범
     * 작성내용: 회원가입을 위한 인증번호 전송
     */
    public AuthNumber sendAuthNumberForSignup(String toNumber, AuthKind authKind) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumberException(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
        }
        return sendAuthNumber(toNumber, authKind);
    }

    /**
     * 작성날짜: 2022/05/25 10:44 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디를 찾기위한 인증번호 전송
     */
    public AuthNumber sendAuthNumberForFindLoginId(String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumberException("서비스에 가입되지 않은 핸드폰 번호입니다.");
        }
        return sendAuthNumber(toNumber, AuthKind.findLoginId);
    }

    /**
     * 작성날짜: 2022/05/25 10:55 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호를 찾기위한 인증번호 전송
     */
    public AuthNumber sendAuthNumberForFindPassword(String loginId, String toNumber) {

        User findUser = userRepository.findByLoginIdAndPhoneNumber(loginId, toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumberException("아이디와 휴대폰번호를 확인해주세요.");
        }
        return sendAuthNumber(toNumber, AuthKind.findPwd);
    }

    /**
     * 작성날짜: 2022/05/24 10:40 AM
     * 작성자: 이승범
     * 작성내용: 인증번호 인증
     */
    public AuthNumber authenticateAuthNum(AuthenticateAuthNumRequest request) {

        AuthNumber authNumber = authNumberRepository
                .findByPhoneNumAndAuthNumAndAuthKind(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAIL));

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > 60) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        } else {
            authNumber.AuthComplete();
        }
        return authNumber;
    }

    /**
     * 작성날짜: 2022/05/24 10:40 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디 찾기
     */
    public String findLoginId(AuthenticateAuthNumRequest request) {

        AuthNumber authNumber = authenticateAuthNum(request);

        User findUser = userRepository.findByPhoneNumber(authNumber.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException("핸드폰 번호를 확인해 주세요"));

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

        // 인증완료된 핸드폰번호인지 확인
        AuthNumber authNumber = validateAuthNumber(request.getPhoneNum(), AuthKind.findPwd);

        User user = userRepository.findByLoginIdAndPhoneNumber(request.getLoginId(), request.getPhoneNum())
                .orElseThrow(() -> new UserException("잘못된 로그인 아이디입니다."));

        user.changePassword(encoder.encode(request.getNewPwd()));
        authNumberRepository.delete(authNumber);
    }

    // 인증이 완료된 인증번호인지 검사
    public AuthNumber validateAuthNumber(String phoneNum, AuthKind authKind){
        AuthNumber authComplete = authNumberRepository.findAuthComplete(phoneNum, authKind).orElse(null);
        if (authComplete == null) {
            throw new SignupException("핸드폰 인증이 완료되지 않았습니다.");
        } else if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 60)) {
            throw new SignupException("핸드폰 인증시간이 만료되었습니다. 핸드폰 인증을 다시 해주세요");
        }
        return authComplete;
    }

    // 인증번호 전송 로직
    private AuthNumber sendAuthNumber(String toNumber, AuthKind authKind) {

        // 4자리 랜덤 숫자 생성
        String authNum = createRandomNumber();

        AuthNumber overlaps = authNumberRepository.findOverlap(toNumber, authKind).orElse(null);

        // 인증번호 최초 요청인 경우 || 이미 인증번호를 받았지만 제한시간이 지난 경우
        if (overlaps == null || Duration.between(overlaps.getCreatedDate(), LocalDateTime.now()).getSeconds() > 60) {

            // 이미 인증번호를 받았지만 제한시간이 지난 경우
            if (overlaps != null) {
                // 예전 인증번호 관련 정보를 db에서 지우고
                authNumberRepository.deleteExpiredNumber(toNumber, authKind);
            }
            // 인증번호 보내고
            requestCoolSMS(toNumber, authNum);
            // 인증번호 관련 정보를 db에 저장
            AuthNumber authNumber = AuthNumber.createAuthNumber(toNumber, authNum, authKind);
            return authNumberRepository.save(authNumber);

        // 이미 인증번호를 요청하였고 제한시간이 지나지 않은 경우
        } else {
            throw new AuthNumberException(AuthNumberErrorResult.YET_AUTHNUMBER_VALID);
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

        this.defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
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

