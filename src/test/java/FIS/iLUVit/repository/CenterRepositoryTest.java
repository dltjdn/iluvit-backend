package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
//@SpringBootTest
@Transactional
class CenterRepositoryTest {

    // TODO find Banner By Id

    // TODO 시설_프리뷰_정보_조회

    // TODO 시설_프리뷰_정보_조회_테마가_전부_null_일때

    // TODO find By Id With Teacher_해당시설에 선생이 등록된 경우

    // TODO find By Id With Teacher_해당시설에 선생이 없는 경우

    // TODO 회원가입을 위한 시설 정보 조회

    @Nested
    @DisplayName("지도 기반 센터 검색")
    class 지도기반센터검색 {

        // TODO 지도 리스트에 나올 정보, 부모 id 안받음 (지도기반 검색하기)

        // TODO 지도 리스트에 나올 정보, 부모 id 안받음 (지도기반 검색하기2)

        // TODO 지도 리스트에 나올 정보 어린이집/유치원으로 분리 (지도기반 검색하기3)

        // TODO 지도에 뿌려줄 센터 정보 (지도에 뿌려줄 센터 정보)

        @Nested
        @DisplayName("센터_베너찾기")
        public class Banner {

            // TODO 특정_시설의_베너정보_찾아오기_로그인_X

            // TODO 특정_시설의_베너정보_찾아오기_로그인_O_시설_북마크_했음

            // TODO 특정_시설의_베너정보_찾아오기_로그인_O_시설_북마크_안했음

            // TODO 특정_시설의_베너정보_찾아오기_로그인_O_선생으로_검색

            // TODO 잘못된_시설_아이디_배너정보_없음
        }

        // TODO find By Prefer

        @Nested
        @DisplayName("학부모 관심 키워드 기반 시설 추천")
        class 학부모관심키워드기반시설추천 {

            // TODO repository에서 시설 추천 dto 가져오기
        }

        @Nested
        @DisplayName("아이추가 시설 정보 검색")
        class findCenterForAddChild {

            // TODO 전체검색

            // TODO area로만 검색

            // TODO 이름으로만 검색

            // TODO paging 검사
        }


        @Nested
        @DisplayName("findByIdAndSignedWithTeacher")
        class findByIdAndSignedWithTeacher {

            // TODO 정상등록된 시설

            // TODO 미등록 시설
        }
    }
}