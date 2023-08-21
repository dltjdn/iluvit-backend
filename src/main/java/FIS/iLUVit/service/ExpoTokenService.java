package FIS.iLUVit.service;

import FIS.iLUVit.dto.expoToken.ExpoTokenDeviceIdRequest;
import FIS.iLUVit.dto.expoToken.ExpoTokenIdResponse;
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

    /**
     * expoToken을 등록합니다
     */
    public ExpoTokenIdResponse saveToken(Long userId, ExpoTokenSaveRequest request) {
        User findUser = userRepository.getById(userId);
        ExpoToken token = ExpoToken.builder()
                .user(findUser)
                .token(request.getToken())
                .accept(request.getAccept())
                .build();
        ExpoToken savedToken = expoTokenRepository.save(token);
        ExpoTokenIdResponse response = new ExpoTokenIdResponse(savedToken.getId());
        return response;
    }

    /**
     * expoToken을 조회합니다
     */
    public ExpoTokenResponse findExpoTokenByUser(Long userId, String expoToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        ExpoToken token = expoTokenRepository.findByTokenAndUser(expoToken, user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        if (!Objects.equals(token.getUser(), user)) {
            throw new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }
        return new ExpoTokenResponse(token);
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
