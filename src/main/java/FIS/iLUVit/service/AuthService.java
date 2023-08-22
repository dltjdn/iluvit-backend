package FIS.iLUVit.service;

import FIS.iLUVit.domain.BlackUser;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.AuthNumberErrorResult;
import FIS.iLUVit.exception.AuthNumberException;
import FIS.iLUVit.repository.AuthRepository;
import FIS.iLUVit.repository.BlackUserRepository;
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
    private final Integer authValidTime = 60;

    // 인증한 후 인증이 유지되는 시간(초)
    private final Integer authNumberValidTime = 60 * 60;

    /**
     * 작성자: 이승범
     * 작성내용: 회원가입을 위한 인증번호 전송
     */
    public AuthNumber sendAuthNumForSignup(String phoneNum) {
        Optional<BlackUser> blackUser = blackUserRepository.findByPhoneNumber(phoneNum);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNum);

        if (blackUser.isPresent() || user.isPresent()) {
            throw new AuthNumberException(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
        }
        return sendAuthNumber(phoneNum, AuthKind.signup, null);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이디찾기를 위한 인증번호 전송
     */
    public AuthNumber sendAuthNumForFindLoginId(String toNumber) {

        userRepository.findByPhoneNumber(toNumber)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_SIGNUP_PHONE));

        return sendAuthNumber(toNumber, AuthKind.findLoginId, null);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 비밀번호찾기를 위한 인증번호 전송
     */
    public AuthNumber sendAuthNumberForFindPassword(String loginId, String toNumber) {

        User findUser = userRepository.findByLoginIdAndPhoneNumber(loginId, toNumber).orElse(null);

        if (findUser == null) {
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_INFO);
        }
        return sendAuthNumber(toNumber, AuthKind.findPwd, null);
    }

    /**
    *   작성자: 이승범
    *   작성내용: 핸드폰번호 변경을 위한 인증번호 전송
    */
    public AuthNumber sendAuthNumForChangePhone(Long id, String toNumber) {

        User findUser = userRepository.findByPhoneNumber(toNumber).orElse(null);

        if (findUser != null) {
            throw new AuthNumberException(AuthNumberErrorResult.ALREADY_PHONENUMBER_REGISTER);
        }
        return sendAuthNumber(toNumber, AuthKind.updatePhoneNum, id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 회원가입, 비밀번호 찾기를 위한 인증번호 인증
     */
    public AuthNumber authenticateAuthNum(AuthNumRequest request) {
        AuthKind authKind = request.getAuthKind();
        if (authKind != AuthKind.signup && authKind != AuthKind.findPwd && authKind != AuthKind.findLoginId) {
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_AUTHKIND);
        }

        AuthNumber authNumber = authRepository
                .findByPhoneNumAndAuthNumAndAuthKind(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAIL));

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        } else {
            authNumber.AuthComplete();
        }
        return authNumber;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 핸드폰번호 변경을 위한 인증번호 인증
     */
    public AuthNumber authenticateAuthNumByPhoneNum(Long userId, AuthNumRequest request) {

        if (! request.getAuthKind().equals(AuthKind.updatePhoneNum)){
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_AUTHKIND);
        }

        AuthNumber authNumber = authRepository
                .findByPhoneNumAndAuthNumAndAuthKindAndUserId(request.getPhoneNum(), request.getAuthNum(), request.getAuthKind(), userId)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.AUTHENTICATION_FAIL));

        if (Duration.between(authNumber.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        } else {
            authNumber.AuthComplete();
        }
        return authNumber;
    }

    /**
     * 작성자: 이승범
     * 작성내용: (아이디찾기) 인증번호 인증 후 유저 아이디 반환
     */
    public String authenticateAuthNumForFindLoginId(AuthNumRequest request) {

        // request와 일치하는 유효한 인증번호가 있는지 검공
        AuthNumber authNumber = authenticateAuthNum(request);

        User findUser = userRepository.findByPhoneNumber(authNumber.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_SIGNUP_PHONE));

        authRepository.delete(authNumber);
        return blindLoginId(findUser.getLoginId());
    }

    /**
     * 작성자: 이승범
     * 작성내용: (비밀번호 변경용 비밀번호찾기) 인증번호 인증 후 비밀번호 변경
     */
    public User authenticateAuthNumForChangePwd(FindPasswordRequest request) {

        // 비밀번호와 비밀번호 확인 불일치
        if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_CHECKPWD);
        }

        // 인증완료된 핸드폰번호인지 확인
        AuthNumber authNumber = validateAuthNumber(request.getPhoneNum(), AuthKind.findPwd);

        User user = userRepository.findByLoginIdAndPhoneNumber(request.getLoginId(), request.getPhoneNum())
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_MATCH_INFO));

        user.changePassword(encoder.encode(request.getNewPwd()));
        authRepository.delete(authNumber);
        return user;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 인증이 완료된 인증번호인지 검사
     */
    public AuthNumber validateAuthNumber(String phoneNum, AuthKind authKind) {
        // 핸드폰 인증여부 확인
        AuthNumber authComplete = authRepository.findAuthComplete(phoneNum, authKind)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_AUTHENTICATION));
        // 핸드폰 인증 후 일정시간이 지나면 무효화
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > authNumberValidTime) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        }
        return authComplete;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 인증번호 전송 로직
     */
    private AuthNumber sendAuthNumber(String toNumber, AuthKind authKind, Long userId) {

        // 4자리 랜덤 숫자 생성
        String authNum = createRandomNumber();

        AuthNumber overlaps = authRepository.findOverlap(toNumber, authKind).orElse(null);

        // 인증번호 최초 요청인 경우 || 이미 인증번호를 받았지만 제한시간이 지난 경우
        if (overlaps == null || Duration.between(overlaps.getCreatedDate(), LocalDateTime.now()).getSeconds() > authValidTime) {

            // 이미 인증번호를 받았지만 제한시간이 지난 경우
            if (overlaps != null) {
                // 예전 인증번호 관련 정보를 db에서 지우고
                authRepository.deleteExpiredNumber(toNumber, authKind);
            }
            // 인증번호 보내고
            requestCoolSMS(toNumber, authNum);
            // 인증번호 관련 정보를 db에 저장
            AuthNumber authNumber = AuthNumber.createAuthNumber(toNumber, authNum, authKind, userId);
            return authRepository.save(authNumber);

            // 이미 인증번호를 요청하였고 제한시간이 지나지 않은 경우
        } else {
            throw new AuthNumberException(AuthNumberErrorResult.YET_AUTHNUMBER_VALID);
        }
    }

    /**
     * 작성자: 이승범
     * 작성내용: 로그인 찾기에서 로그인아이디 일부를 *로 가리기
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
     * 작성자: 이승범
     * 작성내용: CoolSMS 문자전송 요청
     */
    private void requestCoolSMS(String toNumber, String authNumber) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(toNumber);
        message.setText("[아이러빗] 인증번호 " + authNumber + " 를 입력하세요.");

        this.defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
    }

    /**
     * 작성자: 이승범
     * 작성내용: 4자리 랜덤 숫자 생성
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

    public Integer getAuthValidTime() {
        return authValidTime;
    }

    public Integer getAuthNumberValidTime() {
        return authNumberValidTime;
    }

}

