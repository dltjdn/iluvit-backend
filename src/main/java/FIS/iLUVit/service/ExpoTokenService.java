package FIS.iLUVit.service;

import FIS.iLUVit.dto.expoToken.ExpoTokenDto;
import FIS.iLUVit.dto.expoToken.ExpoTokenRequest;
import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ExpoTokenRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
                .accept(request.getAccept())
                .build();
        ExpoToken savedToken = expoTokenRepository.save(token);
        return savedToken.getId();
    }

    public void modifyAcceptStatus(Long userId, ExpoTokenRequest request) {
        ExpoToken expoToken = getExpoTokenWithUserException(request.getToken(), userId);

        expoToken.modifyAcceptStatus(request.getAccept());
    }

    public ExpoTokenDto findById(Long userId, String expoToken) {
        ExpoToken token = getExpoTokenWithUserException(expoToken, userId);
        return new ExpoTokenDto(token.getId(), token.getToken(), token.getAccept());
    }

    @NotNull
    private ExpoToken getExpoTokenWithUserException(String token, Long userId) {
        User user = userRepository.getById(userId);
        ExpoToken expoToken = expoTokenRepository.findByTokenAndUser(token, user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        if (!Objects.equals(expoToken.getUser().getId(), userId)) {
            throw new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }
        return expoToken;
    }

    public void deleteById(Long userId, String expoToken) {
        User user = userRepository.getById(userId);
        expoTokenRepository.deleteByTokenAndUser(expoToken, user);
    }
}
