package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.PatchPasswordRequest;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.InputException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    /**
    *   작성날짜: 2022/05/16 11:57 AM
    *   작성자: 이승범
    *   작성내용: 사용자 기본정보(id, nickname, auth) 반환
    */
    public LoginResponse findUserInfo(Long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));
        return new LoginResponse(findUser);
    }

    /**
    *   작성날짜: 2022/05/16 11:57 AM
    *   작성자: 이승범
    *   작성내용: 비밀번호 변경
    */
    public void updatePassword(Long id, PatchPasswordRequest request) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        if (!encoder.encode(request.getOriginPwd()).equals(findUser.getPassword())) {
            throw new InputException("비밀번호가 틀렸습니다.");
        } else if (request.getNewPwd().equals(request.getNewPwdCheck())) {
            throw new InputException("새로운 비밀번호가 확인과 일치하지 않습니다.");
        }

        findUser.changePassword(encoder.encode(request.getNewPwd()));
    }
}
