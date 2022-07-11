package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;


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
    public void 이미회원가입용인증번호를받았는지() {
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

    public void 실험() {
        // given
        AuthNumber already = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(already);
        em.flush();
        em.clear();

        // when
        AuthNumber over = AuthNumber.createAuthNumber("01067150071", "1234", AuthKind.signup);
        authNumberRepository.save(over);

        // then
        try {
            em.flush();
        } catch (Exception e){
            System.out.println("e.getClass() = " + e.getClass());
        }
//        PersistenceException exception = Assertions.assertThrows(PersistenceException.class, () -> em.flush());
//        Assertions.assertTrue(exception.getCause() instanceof ConstraintViolationException);

    }
}
