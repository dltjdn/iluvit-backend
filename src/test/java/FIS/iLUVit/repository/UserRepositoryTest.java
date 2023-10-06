package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class UserRepositoryTest {

    // TODO 이미 가입된 전화번호인지 확인

    // TODO 로그인 아이디와 휴대폰 번호로 유저 조회

    // TODO 로그인 아이디 또는 닉네임이 같은 사용자 조회

    // TODO find By Id And Phone Number
}
