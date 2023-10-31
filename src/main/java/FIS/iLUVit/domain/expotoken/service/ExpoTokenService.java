package FIS.iLUVit.domain.expotoken.service;

import FIS.iLUVit.domain.expotoken.dto.ExpoTokenDeviceIdRequest;
import FIS.iLUVit.domain.expotoken.dto.ExpoTokenFindOneResponse;
import FIS.iLUVit.domain.expotoken.dto.ExpoTokenSaveRequest;
import FIS.iLUVit.domain.expotoken.domain.ExpoToken;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.expotoken.exception.ExpoTokenErrorResult;
import FIS.iLUVit.domain.expotoken.exception.ExpoTokenException;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.expotoken.repository.ExpoTokenRepository;
import FIS.iLUVit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpoTokenService {

    private final ExpoTokenRepository expoTokenRepository;
    private final UserRepository userRepository;

    /**
     * expoToken을 등록합니다
     */
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

    /**
     * expoToken을 조회합니다
     */
    public ExpoTokenFindOneResponse findExpoTokenByUser(Long userId, String expoToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        ExpoToken token = expoTokenRepository.findByTokenAndUser(expoToken, user)
                .orElseThrow(() -> new ExpoTokenException(ExpoTokenErrorResult.EXPO_TOKEN_NOT_FOUND));

        if (!Objects.equals(token.getUser(), user)) {
            throw new ExpoTokenException(ExpoTokenErrorResult.FORBIDDEN_ACCESS);
        }
        return new ExpoTokenFindOneResponse(token);
    }

    /**
     * expoToken을 삭제합니다
     */
    public void deleteExpoTokenByUser(Long userId, String expoToken) {
        User user = userRepository.getById(userId);
        expoTokenRepository.deleteByTokenAndUser(expoToken, user);
    }

    /**
     * expoToken을 비활성화합니다 ( 회원가입 한 유저가 앱 삭제 후 재설치 할 때 사용 )
     */
    public void deactivateExpoToken(ExpoTokenDeviceIdRequest expoTokenDeviceIdRequest){
        String deviceId = expoTokenDeviceIdRequest.getDeviceId();
        expoTokenRepository.updateExpoTokenDeactivated(deviceId);
    }

    /**
     * 비활성화된 expoToken을 삭제합니다
     */
    public void deleteDeactivatedExpoToken(String deviceId){
        expoTokenRepository.deleteByDeviceIdAndActive(deviceId, false);
    }
}
