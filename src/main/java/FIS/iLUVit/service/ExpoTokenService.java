package FIS.iLUVit.service;

import FIS.iLUVit.dto.expoToken.ExpoTokenDeviceIdRequest;
import FIS.iLUVit.dto.expoToken.ExpoTokenResponse;
import FIS.iLUVit.dto.expoToken.ExpoTokenSaveRequest;
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

    public Long saveToken(Long userId, ExpoTokenSaveRequest request) {
        User findUser = userRepository.getById(userId);
        ExpoToken token = ExpoToken.builder()
                .user(findUser)
                .token(request.getToken())
                .accept(request.getAccept())
                .build();
        ExpoToken savedToken = expoTokenRepository.save(token);
        return savedToken.getId();
    }


    public ExpoTokenResponse findExpoTokenByUser(Long userId, String expoToken) {
        ExpoToken token = getExpoTokenWithUserException(expoToken, userId);
        return new ExpoTokenResponse(token);
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

    public void deleteExpoTokenByUser(Long userId, String expoToken) {
        User user = userRepository.getById(userId);
        expoTokenRepository.deleteByTokenAndUser(expoToken, user);
    }

    /**
     * expoToken 비활성화 ( 회원가입 한 유저가 앱 삭제 후 재설치 할 때 사용 )
     */
    public void deactivateExpoToken(ExpoTokenDeviceIdRequest expoTokenDeviceIdRequest){
        String deviceId = expoTokenDeviceIdRequest.getDeviceId();
        expoTokenRepository.updateExpoTokenDeactivated(deviceId);
    }

    /**
     * 비활성화 된 expoToken을 삭제한다
     */
    public void deleteDeactivatedExpoToken(String deviceId){
        expoTokenRepository.deleteByDeviceIdAndActive(deviceId, false);
    }
}
