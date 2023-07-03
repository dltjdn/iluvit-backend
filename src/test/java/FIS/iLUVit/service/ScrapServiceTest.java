package FIS.iLUVit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScrapServiceTest {

    // TODO 스크랩폴더목록가져오기_성공

    // TODO 스크랩폴더추가하기_성공

    @Nested
    @DisplayName("스크랩 폴더 삭제하기")
    class deleteScrapDir {

        // TODO 잘못된 scrapId

        // TODO default 스크랩 폴더 삭제

        // TODO default 스크랩 폴더 삭제 성공
    }

    // TODO default 스크랩폴더이름바꾸기_성공

    @Nested
    @DisplayName("게시물 스크랩하기")
    class scrapPost{

        // TODO db와 요청이 일치하지않음

        // TODO 잘못된 게시글 아이디

        // TODO 새로 스크랩을 해야되는 경우

        // TODO 기존 스크랩을 취소하는경우

        // TODO 스크랩 취소 및 등록 동시 발생
    }

    @Nested
    @DisplayName("스크랩한 게시물 스크랩폴더에서 삭제")
    class 스크랩폴더에서스크랩삭제{

        // TODO 유효하지 않은 스크랩 포스트 아이디

        // TODO 정상적인 스크랩 포스트 제거
    }

    // TODO 게시물에 대한 스크랩 상태 목록 조회

    // TODO 스크랩게시물_preview_성공
}
