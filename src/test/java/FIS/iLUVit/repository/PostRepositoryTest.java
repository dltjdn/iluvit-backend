package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class PostRepositoryTest {

    // TODO 게시글_아이디로_찾기_페치조인

    // TODO 유저아이디로_게시글_찾기

    // TODO 게시판_별_최신글_TOP_4개_조회

    // TODO 센터_내_좋아요_수_n개_이상_게시글_TOP_4개_조회

    // TODO 모두의_이야기_내_좋아요_수_n개_이상_게시글_TOP_4개_조회

    // TODO 센터_내_키워드_검색

    // TODO 게시판_내_키워드_검색

    // TODO 모두의_이야기_내_핫_게시글_조회

    // TODO 센터_내_핫_게시글_조회

    // TODO 센터_여러개와_모두의_이야기_키워드_검색
}