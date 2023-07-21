package FIS.iLUVit.service;

import FIS.iLUVit.dto.expoToken.ExpoTokenDeviceIdDto;
import FIS.iLUVit.dto.expoToken.ExpoTokenDto;
import FIS.iLUVit.dto.expoToken.ExpoTokenCreateDto;
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

    public Long saveToken(Long userId, ExpoTokenCreateDto request) {
        User findUser = userRepository.getById(userId);
        ExpoToken token = ExpoToken.builder()
                .user(findUser)
                .token(request.getToken())
                .accept(request.getAccept())
                .deviceId(request.getDeviceId())
                .build();
        ExpoToken savedToken = expoTokenRepository.save(token);
        return savedToken.getId();
    }

    public ExpoTokenDto findExpoTokenByUser(Long userId, String expoToken) {
        ExpoToken token = getExpoTokenWithUserException(expoToken, userId);
        return new ExpoTokenDto(token);
    }

    @NotNull
    private ExpoToken getExpoTokenWithUserException(String token, Long userId) {
        User user = userRepository.getById(userId);
        ExpoToken expoToken = expoTokenRepository.findByTokenAndUser(token, user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        if (!expoToken.getUser().getId().equals(userId)) {
            throw new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }
        return expoToken;
    }

    public void deleteExpoTokenByUser(Long userId, String expoToken) {
        User user = userRepository.getById(userId);
        expoTokenRepository.deleteByTokenAndUser(expoToken, user);
    }

    /**
     * expoToken 비활성화 ( 회원가입 한 유저가 앱 삭제 후 재설치 할 때 사용 )
     */
    public void deactivateExpoToken(ExpoTokenDeviceIdDto expoTokenDeviceIdDto){
        String deviceId = expoTokenDeviceIdDto.getDeviceId();
        expoTokenRepository.updateExpoTokenDeactivated(deviceId);
    }

    /**
     * 비활성화 된 expoToken을 삭제한다
     */
    public void deleteDeactivatedExpoToken(String deviceId){
        expoTokenRepository.deleteByDeviceIdAndActive(deviceId, false);
    }

}
