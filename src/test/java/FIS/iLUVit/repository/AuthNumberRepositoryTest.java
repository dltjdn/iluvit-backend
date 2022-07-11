package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class AuthNumberRepositoryTest {

    @Autowired
    private AuthNumberRepository authNumberRepository;
    
    @Autowired
    private EntityManager em;

    @Test
    public void 회원가입용인증번호받기() {
        //given
        AuthNumber authNumber = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);

        //when
        AuthNumber result = authNumberRepository.save(authNumber);
        em.flush();
        em.clear();

        //then
        AuthNumber target = authNumberRepository.findById(result.getId()).orElse(null);
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(result.getId());
    }

    @Test
    public void 회원가입용인증번호를받은적이있는지확인() {
        // given
        AuthNumber already = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(already);
        em.flush();
        em.clear();

        // when
        AuthNumber target = authNumberRepository.findOverlap("01067150071", AuthKind.signup).orElse(null);

        // then
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(already.getId());

    }

    @Test
    public void 이미발급받은인증번호db에서지우기() {
        // given
        AuthNumber already = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(already);
        em.flush();
        em.clear();

        // when
        authNumberRepository.deleteExpiredNumber("01067150071", AuthKind.signup);
        AuthNumber target = authNumberRepository.findOverlap("01067150071", AuthKind.signup).orElse(null);

        // then
        assertThat(target).isNull();
    }

    @Test
    public void 이미인증번호발급받음() {
        // given
        AuthNumber already = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(already);
        em.flush();
        em.clear();

        // when
        AuthNumber over = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(over);

        // then
        PersistenceException exception = assertThrows(PersistenceException.class, () -> em.flush());
        assertTrue(exception.getCause() instanceof ConstraintViolationException);
    }
}
