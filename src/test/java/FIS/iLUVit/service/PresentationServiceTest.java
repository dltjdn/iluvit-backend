package FIS.iLUVit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PresentationServiceTest {

    // TODO 설명회_수정하기

    // TODO 설명회_수정_잘못된_설명회_아이디로_접근시_오류

    // TODO 설명회_수정_승인안된_교사가_수정시_오류

    @Nested
    @DisplayName("셜명회 자세히 보기")
    class 설명회자세히보기{

        // TODO 설명회자세히보기로그인X (설명회 보기 성공 로그인 X)

        // TODO 설명회자세히보기로그인O (설명회 보기 성공 로그인 O)\
    }

    @Nested
    @DisplayName("설명회 저장")
    class 설명회저장{

        // TODO 존재하지않는선생 (설명회 저장시 존재하지 않는 선생님)

        // TODO 설명회작성권한없는경우 (설명회 저장 권한 없음)

        // TODO 유효한설명회존재 (이미 유효한 설명회 존재)

        // TODO 설명회저장성공 (설명회 저장 성공)
    }

    @Nested
    @DisplayName("설명회 수정")
    class 설명회수정{

        // TODO 올바르지않은설명회아이디 (올바르지 않은 설명회 아이디)

        // TODO 존재하지않는선생 (존재하지않는 선생)

        // TODO 권한없는선생 (수정 권한 없는 선생)

        // TODO 설명회회차삭제실패 (설명회 회차 삭제 불가능)

        // TODO 설명회수정성공 (설명회 수정 성공)
    }
}