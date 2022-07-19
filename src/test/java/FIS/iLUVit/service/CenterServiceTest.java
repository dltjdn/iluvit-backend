package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CenterBannerResponseDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static FIS.iLUVit.Creator.createCenter;
import static FIS.iLUVit.Creator.englishAndCoding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CenterServiceTest {

    @Mock
    CenterRepository centerRepository;

    @InjectMocks
    CenterService target;

    @Spy
    ImageService imageService;

    @Nested
    @DisplayName("센터_베너_서비스")
    class BannerServiceTest{

        @Test
        public void 센터_배너_서비스_존재하지않는_센터_아이디() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Center center = createCenter(1L, "test", true,true, theme);
            doReturn(null)
                    .when(centerRepository).findBannerById(2L);
            //when
            CenterBannerResponseDto result = target.findBannerById(2L, null);

            //then
            assertThat(result)
                    .isNull();

        }

        @Test
        public void 센터_배너_서비스_테스트_로그인_O() throws Exception {
            //given
            CenterBannerDto centerBannerDto = new CenterBannerDto(1L, "test", true, true, 4.5, 1L,"testLocation", "fdfd");
            doReturn(centerBannerDto)
                    .when(centerRepository).findBannerById(any(Long.class), any(Long.class));

            //when
            CenterBannerResponseDto result = target.findBannerById(1L, 1L);
            //then
            verify(centerRepository, times(1))
                    .findBannerById(any(Long.class), any(Long.class));
            assertThat(result.getPrefer()).isTrue();
            assertThat(result.getSigned()).isTrue();
        }

        @Test
        public void 센터_배너_서비스_테스트_로그인_X() throws Exception {
            //given
            CenterBannerDto centerBannerDto = new CenterBannerDto(1L, "test", true, true, 4.5, null,"testLocation");
            doReturn(centerBannerDto)
                    .when(centerRepository).findBannerById(any(Long.class));

            //when
            CenterBannerResponseDto result = target.findBannerById(1L, null);
            //then
            verify(centerRepository, times(1))
                    .findBannerById(any(Long.class));
            assertThat(result.getPrefer()).isFalse();
            assertThat(result.getSigned()).isTrue();
        }
    }
}
