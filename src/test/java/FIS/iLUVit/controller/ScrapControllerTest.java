package FIS.iLUVit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScrapControllerTest {

    // TODO 스크랩폴더목록정보가져오기_성공

    @Nested
    @DisplayName("스크랩폴더추가하기")
    class addScrap{

        // TODO 스크랩폴더추가하기_성공

        // TODO 스크랩폴더추가하기_실패_불완전한요청

    }

    @Nested
    @DisplayName("스크랩 폴더 삭제하기")
    class deleteScrapDir{

        // TODO 잘못된스크랩아이디

        // TODO 스크랩폴더삭제성공

    }
    @Nested
    @DisplayName("스크랩폴더이름바꾸기")
    class changeScrapName{

        // TODO 불완전한요청

        // TODO 잘못된스크랩아이디

        // TODO 이름바꾸기성공

    }

    @Nested
    @DisplayName("게시물 스크랩하기")
    class scrapPost{

        // TODO 불완전한요청

        // TODO 게시물아이디 오류

        // TODO 스크랩아이디 오류

        // TODO 게시물스크랩성공

    }

    @Nested
    @DisplayName("스크랩취소")
    class deleteScrapPost{

        // TODO 잘못된 scrapPost

        // TODO 스크랩취소성공

    }

    // TODO 게시물에대한스크랩목록조회_성공

    // TODO 스크램게시물프리뷰_성공

}