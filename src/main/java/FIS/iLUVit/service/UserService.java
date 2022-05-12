package FIS.iLUVit.service;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public LoginResponse findUserInfo(Long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("존재하지 않는 사용자입니다."));
        return new LoginResponse(findUser);
    }
}
