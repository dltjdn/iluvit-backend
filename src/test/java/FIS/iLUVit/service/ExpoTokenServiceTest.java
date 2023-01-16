package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ExpoTokenRequest;
import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.ExpoTokenRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExpoTokenServiceTest {
    @InjectMocks
    ExpoTokenService expoTokenService;

    @Mock
    ExpoTokenRepository expoTokenRepository;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("엑스포 토큰 서버에 저장")
    public void save() throws Exception {
        //given
        ExpoTokenRequest request = new ExpoTokenRequest();
        request.setToken("ExponentPushToken[FeQrt0GvJiT-1i1ClIgINc]");

        ExpoToken token = ExpoToken.builder()
                .id(2L)
                .token("ExponentPushToken[FeQrt0GvJiT-1i1ClIgINc]")
                .build();

        User user = Parent.builder()
                .id(1L)
                .build();

        given(userRepository.getById(any()))
                .willReturn(user);
        given(expoTokenRepository.save(any()))
                .willReturn(token);

        //when
        Long tokenId = expoTokenService.saveToken(user.getId(), request);
        //then
        assertThat(tokenId).isEqualTo(token.getId());
    }

}