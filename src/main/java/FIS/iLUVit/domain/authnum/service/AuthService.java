package FIS.iLUVit.domain.authnum.service;

import FIS.iLUVit.domain.blackuser.domain.BlackUser;
import FIS.iLUVit.domain.authnum.dto.AuthRequest;
import FIS.iLUVit.domain.authnum.dto.AuthFindPasswordRequest;
import FIS.iLUVit.domain.authnum.domain.AuthNumber;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.authnum.domain.AuthKind;
import FIS.iLUVit.domain.authnum.exception.AuthNumberErrorResult;
import FIS.iLUVit.domain.authnum.exception.AuthNumberException;
import FIS.iLUVit.domain.authnum.repository.AuthRepository;
import FIS.iLUVit.domain.blackuser.repository.BlackUserRepository;
import FIS.iLUVit.domain.user.repository.UserRepository;
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
import java.util.Optional;
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
    private final BlackUserRepository blackUserRepository;

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
        Optional<BlackUser> blackUser = blackUserRepository.findByPhoneNumber(toNumber);
        Optional<User> user = userRepository.findByPhoneNumber(toNumber);

        if (blackUser.isPresent() || user.isPresent()) {
            throw new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_ALREADY_REGISTERED);
        }
        sendAuthNumber(toNumber, AuthKind.signup, null);
    }

    /**
     * (아이디찾기) 인증번호 받기
     */
    public void sendAuthNumForFindLoginId(String toNumber) {

        userRepository.findByPhoneNumber(toNumber)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_NOT_REGISTERED));

        sendAuthNumber(toNumber, AuthKind.findLoginId, null);
    }

    /**
     * (비밀번호찾기) 인증번호 받기
     */
    public void sendAuthNumberForFindPassword(String loginId, String toNumber) {

        userRepository.findByLoginIdAndPhoneNumber(loginId, toNumber)
                .orElseThrow(()-> new AuthNumberException(AuthNumberErrorResult.ID_OR_PASSWORD_MISMATCH));

        sendAuthNumber(toNumber, AuthKind.findPwd, null);
    }

   /**
    * (핸드폰 번호 변경) 인증번호 받기
    */
    public void sendAuthNumForChangePhone(Long userId, String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_ALREADY_REGISTERED);
        }
        sendAuthNumber(toNumber, AuthKind.updatePhoneNum, userId);
    }

    /**
     * 회원가입, 비밀번호 찾기를 위한 인증번호 인증
     */
    public AuthNumber authenticateAuthNum(AuthRequest request) {
        AuthKind authKind = request.getAuthKind();
        if (authKind != AuthKind.signup && authKind != AuthKind.findPwd && authKind != AuthKind.findLoginId) {
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_KIND_MISMATCH);
        }

        AuthNumber authNumber = authRepository
                .findByPhoneNumAndAuthNumAndAuthKind(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAILED));

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_NUMBER_EXPIRED);
        }

        authNumber.AuthComplete();

        return authNumber;
    }

    /**
     * 핸드폰번호 변경을 위한 인증번호 인증
     */
    public AuthNumber authenticateAuthNumForChangingPhoneNum(Long userId, AuthRequest request) {

        if (! request.getAuthKind().equals(AuthKind.updatePhoneNum)){
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_KIND_MISMATCH);
        }

        AuthNumber authNumber = authRepository
                .findByPhoneNumAndAuthKindAndAuthNumAndUserId(request.getPhoneNum(), request.getAuthKind(),request.getAuthNum(), userId)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAILED));

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_NUMBER_EXPIRED);
        } else {
            authNumber.AuthComplete();
        }
        return authNumber;
    }


    /**
     * (아이디찾기) 인증번호 인증 후 유저 아이디 반환
     */
    public String authenticateAuthNumForFindLoginId(AuthRequest request) {

        // 인증번호 인증
        AuthNumber authNumber = authenticateAuthNum(request);

        User findUser = userRepository.findByPhoneNumber(authNumber.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_NOT_REGISTERED));

        // 인증정보 삭제
        authRepository.delete(authNumber);

        return blindLoginId(findUser.getLoginId());
    }

    /**
     * (비밀번호 변경용 비밀번호찾기) 인증이 완료된 핸드폰번호인지 확인 후 비밀번호 변경
     */
    public void authenticateAuthNumForChangePwd(AuthFindPasswordRequest request) {
        // 비밀번호와 비밀번호 확인 불일치
        if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new AuthNumberException(AuthNumberErrorResult.PASSWORD_MISMATCH);
        }

        // 인증완료된 핸드폰번호인지 확인
        AuthNumber authNumber = validateAuthNumber(request.getPhoneNum(), AuthKind.findPwd);

        User user = userRepository.findByLoginIdAndPhoneNumber(request.getLoginId(), request.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.ID_OR_PASSWORD_MISMATCH));

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
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.PHONE_NUMBER_UNVERIFIED));

        // 핸드폰 인증 후 일정시간이 지나면 무효화
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > authNumberValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_NUMBER_EXPIRED);
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
            throw new AuthNumberException(AuthNumberErrorResult.AUTH_NUMBER_IN_PROGRESS);
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

