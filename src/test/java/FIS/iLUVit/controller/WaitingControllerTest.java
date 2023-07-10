package FIS.iLUVit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WaitingControllerTest {

    @Nested
    @DisplayName("설명회 대기 신청")
    class 설명회대기신청{

        // TODO 로그인이 안되어 있을 경우 Error 발생

        // TODO 잘못 요청시 오류 발생

        // TODO 서비스 계층 오류 잘못된 ptDate 요청

        // TODO 설명회 신청기간이 지났을 경우

        // TODO 대기등록을 이미 했을 경우

        // TODO 설명회 인원이 가득 차지 않았을 경우

        // TODO 이미 설명회 신청자가 대기 신청

        // TODO 대기 등록 성공

    }

    @Nested
    @DisplayName("설명회 대기 취소")
    class 설명회대기취소{

        // TODO 로그인 안했음

        // TODO 잘못 요청 시 오류 발생

        // TODO 잘못된 대기 요청 취소 service 에서 발생

        // TODO 설명회 취소 성공

    }
}
