package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
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
        AuthNumber target = authNumberRepository.findById(result.getId()).get();
        assertThat(target.getId()).isEqualTo(result.getId());
    }

    @Test
    public void 이미() {
        // given

        // when

        // then

    }
}
