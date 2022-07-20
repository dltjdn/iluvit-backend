package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.controller.dto.UpdatePasswordRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthNumberRepository authNumberRepository;
    private final AlarmRepository alarmRepository;

    /**
    *   작성날짜: 2022/05/16 11:57 AM
    *   작성자: 이승범
    *   작성내용: 사용자 기본정보(id, nickname, auth) 반환
    */
    public LoginResponse findUserInfo(Long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));
        return findUser.getUserInfo();
    }

    /**
    *   작성날짜: 2022/05/16 11:57 AM
    *   작성자: 이승범
    *   작성내용: 비밀번호 변경
    */
    public void updatePassword(Long id, UpdatePasswordRequest request) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        if (!encoder.matches(request.getOriginPwd(), findUser.getPassword())) {
            throw new InputException("비밀번호가 틀렸습니다.");
        } else if (!request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new InputException("새로운 비밀번호가 확인과 일치하지 않습니다.");
        }

        findUser.changePassword(encoder.encode(request.getNewPwd()));
    }

    // 회원가입 학부모, 교사 공통 로직(유효성 검사 및 비밀번호 해싱)
    public String signupValidation(String password, String passwordCheck, String loginId, String phoneNum, String nickName) {

        // 비밀번호 확인
        if (!password.equals(passwordCheck)) {
            throw new SignupException(SignupErrorResult.NOT_MATCH_PWDCHECK);
        }

        // 로그인 아이디, 닉네임 중복확인
        User duplicatedUser = userRepository.findByLoginIdOrNickName(loginId, nickName).orElse(null);
        if (duplicatedUser != null) {
            throw new SignupException(SignupErrorResult.DUPLICATED_NICKNAME);
        }

        // 핸드폰 인증확인
        AuthNumber authComplete = authNumberRepository.findAuthComplete(phoneNum, AuthKind.signup)
                .orElseThrow(() -> new AuthNumberException(AuthNumberErrorResult.NOT_AUTHENTICATION));

        // 핸드폰 인증후 너무 많은 시간이 지났으면 인증 무효
        if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 60)) {
            throw new AuthNumberException(AuthNumberErrorResult.EXPIRED);
        }

        return encoder.encode(password);
    }

    public Slice<AlarmDto> findUserAlarm(Long userId, Pageable pageable) {
        Slice<Alarm> alarmSlice = alarmRepository.findByUser(userId, pageable);
        return new SliceImpl<>(alarmSlice.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarmSlice.hasNext());
    }

    public Integer deleteUserAlarm(Long userId, Long alarmId) {
        return alarmRepository.deleteById(userId, alarmId);
    }
}
