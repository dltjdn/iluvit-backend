package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CenterBannerResponse;
import FIS.iLUVit.controller.dto.CenterDetailRequest;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CenterServiceTest {

    @Mock
    CenterRepository centerRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    MapService mapService;

    @InjectMocks
    CenterService target;

    @Spy
    ImageServiceStubAmazon imageService;

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
            CenterBannerResponse result = target.findBannerById(2L, null);

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
            CenterBannerResponse result = target.findBannerById(1L, 1L);
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
            CenterBannerResponse result = target.findBannerById(1L, null);
            //then
            verify(centerRepository, times(1))
                    .findBannerById(any(Long.class));
            assertThat(result.getPrefer()).isFalse();
            assertThat(result.getSigned()).isTrue();
        }
    }

    @Nested
    @DisplayName("센터 지도 기반으로 검색하기")
    class 센터지도기반으로검색하기{
        @Test
        @DisplayName("[success] 위경도 기반으로 검색하기 성공 데이터 있음")
        public void 위경도로검색1() throws Exception {
            //given
            Center center1 = createCenter("이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createCenter("현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createCenter("이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createCenter("김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createCenter("신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createCenter("한명수 어린이집", 3, 37.5105178, 127.0147458);
            List<CenterAndDistancePreview> data = new ArrayList<>();
            data.add(new CenterAndDistancePreview(center1, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center2, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center3, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center4, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center5, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center6, 2.3, 1L));
            SliceImpl<CenterAndDistancePreview> dataSlice = new SliceImpl<>(data, PageRequest.of(0, 10), false);
            List<Long> centerIds = new ArrayList<>();
            centerIds.add(center1.getId());
            centerIds.add(center2.getId());
            centerIds.add(center3.getId());
            centerIds.add(center4.getId());
            centerIds.add(center5.getId());
            centerIds.add(center6.getId());

            Mockito.doReturn(dataSlice).when(centerRepository).findByFilterForMapList(1.2, 1.2, 1L, KindOf.ALL, centerIds, PageRequest.of(0, 10));

            //when
            List<CenterAndDistancePreview> result = target.findByFilterForMapList(1.2, 1.2, centerIds, 1L, KindOf.ALL, PageRequest.of(0, 10)).getContent();

            //then
            verify(centerRepository, times(1)).findByFilterForMapList(1.2, 1.2, 1L, KindOf.ALL, centerIds, PageRequest.of(0, 10));
            assertThat(result.size()).isEqualTo(6);
        }

        @Test
        @DisplayName("[success] 위경도 기반으로 검색 자료가 없을 경우 빈 배열 반환")
        public void 자료없으면빈배열반환() throws Exception {
            //given
            List<CenterAndDistancePreview> data = new ArrayList<>();
            SliceImpl<CenterAndDistancePreview> dataSlice = new SliceImpl<>(data, PageRequest.of(0, 10), false);
            List<Long> centerIds = new ArrayList<>();
            Mockito.doReturn(dataSlice).when(centerRepository).findByFilterForMapList(1.2, 1.2, 1L, KindOf.ALL, centerIds, PageRequest.of(0, 10));

            //when
            List<CenterAndDistancePreview> result = target.findByFilterForMapList(1.2, 1.2, new ArrayList<>(), 1L, KindOf.ALL, PageRequest.of(0, 10)).getContent();

            //then
            verify(centerRepository, times(1)).findByFilterForMapList(1.2, 1.2, 1L, KindOf.ALL, centerIds, PageRequest.of(0, 10));
            assertThat(result.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("[success] 위경도 기반으로 검색 로그인 X")
        public void 위경도기반검색로그인X() throws Exception {
            //given
            Center center1 = createCenter(1L, "이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createCenter(2L,"현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createCenter(3L,"이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createCenter(4L,"김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createCenter(5L,"신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createCenter(6L,"한명수 어린이집", 3, 37.5105178, 127.0147458);
            List<CenterAndDistancePreview> data = new ArrayList<>();
            data.add(new CenterAndDistancePreview(center1, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center2, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center3, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center4, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center5, 2.3, 1L));
            data.add(new CenterAndDistancePreview(center6, 2.3, 1L));
            SliceImpl<CenterAndDistancePreview> dataSlice = new SliceImpl<>(data, PageRequest.of(0, 10), false);
            List<Long> centerIds = new ArrayList<>();
            centerIds.add(center1.getId());
            centerIds.add(center2.getId());
            centerIds.add(center3.getId());
            centerIds.add(center4.getId());
            centerIds.add(center5.getId());
            centerIds.add(center6.getId());


            Mockito.doReturn(dataSlice).when(centerRepository).findByFilterForMapList(1.2, 1.2, KindOf.ALL, centerIds, PageRequest.of(0, 10));

            //when
            List<CenterAndDistancePreview> result = target.findByFilterForMapList(1.2, 1.2, centerIds, null, KindOf.ALL,PageRequest.of(0, 10)).getContent();

            //then
            verify(centerRepository, times(1)).findByFilterForMapList(1.2, 1.2,  KindOf.ALL, centerIds, PageRequest.of(0, 10));
            assertThat(result.size()).isEqualTo(6);
        }

    }

    @Nested
    @DisplayName("시설 수정하기")
    class 시설수정하기{

        MultipartFile multipartFile;
        List<MultipartFile> multipartFileList = new ArrayList<>();
        Center center;
        Teacher waitingTeacher;
        Teacher acceptTeacher;
        @BeforeEach
        public void init() throws IOException {
            String name = "162693895955046828.png";
            Path path1 = Paths.get(new File("").getAbsolutePath() + '/' + name);
            byte[] content = Files.readAllBytes(path1);
            multipartFile = new MockMultipartFile(name, name, "image", content);
            multipartFileList.add(multipartFile);
            multipartFileList.add(multipartFile);
            center = createCenter(1L, "test");
            waitingTeacher = createTeacher(1L, center, Auth.TEACHER, Approval.WAITING);
            acceptTeacher = createTeacher(1L, center, Auth.TEACHER, Approval.ACCEPT);
        }

        @Test
        @DisplayName("[error] 시설을 수정하려는 유저 정보없음")
        public void 시설수정하는유저정보X() throws Exception {
            //given
            Mockito.doReturn(Optional.empty())
                    .when(userRepository).findTeacherById(1L);
            //when
            UserException result = Assertions.assertThrows(UserException.class,
                    () -> target.modifyCenter(1L, 1L, new CenterDetailRequest(), multipartFileList, multipartFile));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.USER_NOT_EXIST);

        }

        @Test
        @DisplayName("[error] 시설을 수정하려는 권한 없음")
        public void 시설수정권한없음() throws Exception {
            //given
            Mockito.doReturn(Optional.of(waitingTeacher))
                    .when(userRepository).findTeacherById(1L);
            //when
            CenterException result = Assertions.assertThrows(CenterException.class,
                    () -> target.modifyCenter(1L, 1L, new CenterDetailRequest(), multipartFileList, multipartFile));

            //then
            assertThat(result.getErrorResult()).isEqualTo(CenterErrorResult.AUTHENTICATION_FAILED);
        }

        @Test
        @DisplayName("[error] 잘못된 주소 입력으로 인한 오류 발생")
        public void 잘못된주소입력시오류발생() throws Exception {
            //given
            Mockito.doReturn(Optional.of(acceptTeacher))
                    .when(userRepository).findTeacherById(1L);
            CenterDetailRequest request = new CenterDetailRequest();
            request.setAddress("잘못된 주소");
            Mockito.doThrow(new CenterException(CenterErrorResult.CENTER_WRONG_ADDRESS))
                    .when(mapService).convertAddressToLocation("잘못된 주소");

            //when
            CenterException result = Assertions.assertThrows(CenterException.class,
                    () -> target.modifyCenter(1L, 1L, request, multipartFileList, multipartFile));
            //then
            assertThat(result.getErrorResult()).isEqualTo(CenterErrorResult.CENTER_WRONG_ADDRESS);
        }

        @Test
        @DisplayName("[success] 센터 수정 성공")
        public void 센터수정성공() throws Exception {
            //given
            Mockito.doReturn(Optional.of(acceptTeacher))
                    .when(userRepository).findTeacherById(1L);
            CenterDetailRequest request = new CenterDetailRequest();
            request.setAddress("서울특별시 금천구 가산디지털2로 108 뉴티캐슬");
            //when
            Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                    .when(mapService).convertAddressToLocation("서울특별시 금천구 가산디지털2로 108 뉴티캐슬");
            Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                    .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);

            target.modifyCenter(1L, 1L, request, multipartFileList, multipartFile);

            //then
            assertThat(center.getLongitude()).isEqualTo(126.8806602);
            assertThat(center.getLatitude()).isEqualTo(37.4778951);
            assertThat(center.getAddress()).isEqualTo("서울특별시 금천구 가산디지털2로 108 뉴티캐슬");
            verify(imageService, times(1)).saveInfoImages(multipartFileList, center);
            verify(imageService, times(1)).saveProfileImage(multipartFile, center);
            System.out.println("center.getProfileImagePath() = " + center.getProfileImagePath());
            System.out.println("center = " + center.getInfoImagePath());
        }
    }

}
