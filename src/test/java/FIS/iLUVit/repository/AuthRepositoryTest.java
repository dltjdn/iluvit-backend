package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class AuthRepositoryTest {

    // TODO 회원가입용 인증번호 받기

    // TODO find Over lap

    // TODO delete Expired Number

    // TODO 이미 인증번호 발급 받음

    // TODO find By Phone Num And Auth Num And Auth Kind

    // TODO find Auth Complete 인증된거 없음

    // TODO find Auth Complete 인증된거 있음

    // TODO delete By Phone Num And Auth Kind

    // TODO find By Phone Num And Auth Num And Auth Kind And User Id
}
