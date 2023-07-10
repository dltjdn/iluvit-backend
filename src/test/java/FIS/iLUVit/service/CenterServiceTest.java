package FIS.iLUVit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CenterServiceTest {
    @Nested
    @DisplayName("센터_베너_서비스")
    class BannerServiceTest{

        // TODO 센터_배너_서비스_존재하지않는_센터_아이디

        // TODO 센터_배너_서비스_테스트_로그인_O

        // TODO 센터_배너_서비스_테스트_로그인_X
    }

    @Nested
    @DisplayName("센터 지도 기반으로 검색하기")
    class 센터지도기반으로검색하기{

        // TODO 위경도로 검색1(위경도 기반으로 검색하기 성공 데이터 있음)

        // TODO 자료 없으면 빈배열 반환(위경도 기반으로 검색 자료가 없을 경우 빈 배열 반환)

        // TODO 위경도 기반 검색 로그인 X(위경도 기반으로 검색 로그인 X)
    }

    @Nested
    @DisplayName("시설 수정하기")
    class 시설수정하기{

        // TODO 시설 수정하는 유저 정보 X(시설을 수정하려는 유저 정보없음)

        // TODO 시설 수정 권한 없음(시설을 수정하려는 권한 없음)

        // TODO 잘못된 주소 입력시 오류 발생(잘못된 주소 입력으로 인한 오류 발생)

        // TODO 센터 수정 성공(센터 수정 성공)
    }
}
