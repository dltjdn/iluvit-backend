package FIS.iLUVit.service;

import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindLoginIdDto;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AuthRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
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
public class AuthService {

    private final MessageService defaultMessageService;
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    // 인증번호 제한시간(초)
    private final Integer authValidTime = 120;

    // 인증한 후 인증이 유지되는 시간(초)
    private final Integer authNumberValidTime = 60 * 60;

    /**
     * (회원가입) 인증번호 받기
     */
    public void sendAuthNumForSignup(String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumberException(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
        }
        sendAuthNumber(toNumber, AuthKind.signup, null);
    }

    /**
     * (아이디찾기) 인증번호 받기
     */
    public void sendAuthNumForFindLoginId(String toNumber) {

        userRepository.findByPhoneNumber(toNumber)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_SIGNUP_PHONE));

        sendAuthNumber(toNumber, AuthKind.findLoginId, null);
    }

    /**
     * (비밀번호찾기) 인증번호 받기
     */
    public void sendAuthNumberForFindPassword(String loginId, String toNumber) {

        User findUser = userRepository.findByLoginIdAndPhoneNumber(loginId, toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_INFO);
        }
        sendAuthNumber(toNumber, AuthKind.findPwd, null);
    }

   /**
    * (핸드폰 번호 변경) 인증번호 받기
    */
    public void sendAuthNumForChangePhone(Long userId, String toNumber) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumberException(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
        }
        sendAuthNumber(toNumber, AuthKind.updatePhoneNum, userId);
    }

    /**
     * (회원가입, 비밀번호 찾기, 핸드폰번호 변경) 인증번호 인증
     */
    public AuthNumber authenticateAuthNum(Long userId, AuthNumRequest request) {

        AuthNumber authNumber;

        if (request.getAuthKind().equals(AuthKind.updatePhoneNum)) { // 핸드폰번호 변경 인증번호 인증
            authNumber = authRepository
                    .findByPhoneNumAndAuthKindAndAuthNumAndUserId(request.getPhoneNum(), request.getAuthKind(),request.getAuthNum(), userId)
                    .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAIL));

        } else { // 회원가입 or 비밀번호 찾기 인증번호 인증
            authNumber = authRepository
                    .findByPhoneNumAndAuthNumAndAuthKind(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind())
                    .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAIL));
        }

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        }

        authNumber.AuthComplete(); // 인증을 완료한다 ( 인증 시간을 기록한다 )

        return authNumber;
    }

    /**
     * (아이디찾기) 인증번호 인증 후 유저 아이디 반환
     */
    public FindLoginIdDto authenticateAuthNumForFindLoginId(AuthNumRequest request) {

        // 인증번호 인증
        AuthNumber authNumber = authenticateAuthNum(null, request);

        User findUser = userRepository.findByPhoneNumber(authNumber.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_SIGNUP_PHONE));

        // 인증정보 삭제
        authRepository.delete(authNumber);

        String blindLoginId = blindLoginId(findUser.getLoginId());

        FindLoginIdDto findLoginIdDto = new FindLoginIdDto(blindLoginId);

        return findLoginIdDto;
    }

    /**
     * (비밀번호 변경용 비밀번호찾기) 인증이 완료된 핸드폰번호인지 확인 후 비밀번호 변경
     */
    public void authenticateAuthNumForChangePwd(FindPasswordRequest request) {
        // 비밀번호와 비밀번호 확인 불일치
        if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_CHECKPWD);
        }

        // 인증완료된 핸드폰번호인지 확인
        AuthNumber authNumber = validateAuthNumber(request.getPhoneNum(), AuthKind.findPwd);

        User user = userRepository.findByLoginIdAndPhoneNumber(request.getLoginId(), request.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_INFO));

        // 비밀 번호 변경
        user.changePassword(encoder.encode(request.getNewPwd()));

        // 인증정보 삭제
        authRepository.delete(authNumber);
    }

    /**
     * 인증이 완료된 핸드폰번호인지 확인
     */
    public AuthNumber validateAuthNumber(String phoneNum, AuthKind authKind) {
        // 핸드폰 인증여부 확인
        AuthNumber authComplete = authRepository.findByPhoneNumAndAuthKindAndAuthTimeNotNull(phoneNum, authKind)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_AUTHENTICATION));

        // 핸드폰 인증 후 일정시간이 지나면 무효화
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > authNumberValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        }
        return authComplete;
    }

    /**
        인증 번호 전송 ( 인증 번호 받기 )
    */
    private void sendAuthNumber(String toNumber, AuthKind authKind, Long userId) {

        // 4자리 랜덤 숫자 생성
        String authNum = createRandomNumber();

        AuthNumber auth = authRepository.findByPhoneNumAndAuthKind(toNumber, authKind).orElse(null);

        // 인증번호 최초 요청인 경우 || 이미 인증번호를 받았지만 제한시간이 지난 경우
        if (auth == null || Duration.between(auth.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {

            // 이미 인증번호를 받았지만 제한시간이 지난 경우
            if (auth != null) {
                // 예전 인증번호 관련 정보를 db에서 지우고
                authRepository.deleteByPhoneNumAndAuthKind(toNumber, authKind);
            }
            // 인증번호 보내고
            requestCoolSMS(toNumber, authNum);

            // 인증번호 관련 정보를 db에 저장
            AuthNumber authNumber = AuthNumber.createAuthNumber(toNumber, authNum, authKind, userId);
            authRepository.save(authNumber);

        } else {  // 이미 인증번호를 요청하였고 제한시간이 지나지 않은 경우
            throw new AuthNumberException(AuthNumberErrorResult.YET_AUTHNUMBER_VALID);
        }
    }

    /**
     *  로그인 아이디 일부를 *로 가리기
     */
    private String blindLoginId(String loginId) {
        StringBuilder builder = new StringBuilder(loginId);
        int mid = loginId.length() / 2;
        builder.setCharAt(mid, '*');
        builder.setCharAt(mid - 1, '*');
        builder.setCharAt(mid + 1, '*');
        return builder.toString();
    }

    /**
     * CoolSMS 문자전송 요청
     */
    private void requestCoolSMS(String toNumber, String authNumber) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[아이러빗] 인증번호 " + authNumber + " 를 입력하세요.");

        this.defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
    }

    /**
     * 4자리 랜덤 숫자 생성
     */
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

