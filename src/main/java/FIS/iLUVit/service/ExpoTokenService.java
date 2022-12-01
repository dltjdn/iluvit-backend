package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ExpoTokenRequest;
import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ExpoTokenRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpoTokenService {

    private final ExpoTokenRepository expoTokenRepository;
    private final UserRepository userRepository;

    public Long saveToken(Long userId, ExpoTokenRequest request) {
        User findUser = userRepository.getById(userId);
        ExpoToken token = ExpoToken.builder()
                .user(findUser)
                .token(request.getToken())
                .build();
        ExpoToken savedToken = expoTokenRepository.save(token);
        return savedToken.getId();
    }

    public void deleteToken(Long userId, ExpoTokenRequest request) {
        User findUser = userRepository.getById(userId);
        ExpoToken expoToken = expoTokenRepository.findByUserAndToken(findUser, request.getToken())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        expoTokenRepository.delete(expoToken);
    }
}
