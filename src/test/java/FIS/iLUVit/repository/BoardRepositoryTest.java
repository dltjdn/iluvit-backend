package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Kindergarten;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class BoardRepositoryTest {

    // TODO 게시판 저장

    // TODO 게시판 조회

    // TODO 게시판 삭제

    // TODO 센터로 게시판 조회

    // TODO 모두의 이야기 게시판 조회

    // TODO 이름으로 게시판 조회

    // TODO Default 게시판 조회

    // TODO 모두의이야기 default 게시판 조회

    // TODO find By Center
}