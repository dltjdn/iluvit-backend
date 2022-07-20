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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class AuthNumberRepositoryTest {

    @Autowired
    private AuthNumberRepository authNumberRepository;

    @Autowired
    private EntityManager em;

    AuthNumber authNumber1;
    AuthNumber authNumber2;
    AuthNumber authNumber3;
    AuthNumber authNumber4;
    User user;
    String phoneNum1 = "phoneNumber1";
    String phoneNum2 = "phoneNumber2";
    String authNum = "1234";

    @BeforeEach
    void init() {
        authNumber1 = AuthNumber.createAuthNumber(phoneNum1, authNum, AuthKind.signup, null);
        authNumber2 = AuthNumber.createAuthNumber(phoneNum1, authNum, AuthKind.findLoginId, null);
        authNumber3 = AuthNumber.createAuthNumber(phoneNum2, authNum, AuthKind.signup, null);
        authNumber4 = AuthNumber.createAuthNumber(phoneNum2, authNum, AuthKind.findLoginId, null);
        user = Parent.builder()
                .phoneNumber(phoneNum1)
                .build();
    }


    @Test
    public void 회원가입용인증번호받기() {
        //given

        //when
        AuthNumber result = authNumberRepository.save(authNumber1);
        em.flush();
        em.clear();

        //then
        AuthNumber target = authNumberRepository.findById(result.getId()).orElse(null);
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(result.getId());
    }

    @Test
    public void findOverlap() {
        // given
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        em.flush();
        em.clear();

        // when
        AuthNumber target = authNumberRepository.findOverlap(phoneNum1, AuthKind.signup).orElse(null);

        // then
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(authNumber1.getId());

    }

    @Test
    public void deleteExpiredNumber() {
        // given
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        authNumberRepository.save(authNumber3);
        authNumberRepository.save(authNumber4);
        em.flush();
        em.clear();

        // when
        authNumberRepository.deleteExpiredNumber(phoneNum1, AuthKind.signup);
        AuthNumber target = authNumberRepository.findOverlap(phoneNum1, AuthKind.signup).orElse(null);

        // then
        assertThat(target).isNull();
    }

    @Test
    public void 이미인증번호발급받음() {
        // given
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        authNumberRepository.save(authNumber3);
        authNumberRepository.save(authNumber4);
        em.flush();
        em.clear();

        // when
        AuthNumber over = AuthNumber.createAuthNumber(phoneNum1, authNum, AuthKind.signup, null);
        authNumberRepository.save(over);

        // then
        PersistenceException exception = assertThrows(PersistenceException.class, () -> em.flush());
        assertTrue(exception.getCause() instanceof ConstraintViolationException);
    }

    @Test
    public void findByPhoneNumAndAuthNumAndAuthKind() {
        // given
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        authNumberRepository.save(authNumber3);
        authNumberRepository.save(authNumber4);
        em.flush();
        em.clear();

        // when
        AuthNumber target = authNumberRepository.findByPhoneNumAndAuthNumAndAuthKind(phoneNum1, authNum, AuthKind.signup)
                .orElse(null);

        // then
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(authNumber1.getId());
    }

    @Test
    public void findAuthComplete_인증된거없음() {
        // given
        AuthNumber authNumber = Creator.createAuthNumber(phoneNum1, authNum, AuthKind.findPwd, null);
        authNumberRepository.save(authNumber);
        em.flush();
        em.clear();
        // when
        AuthNumber target = authNumberRepository.findAuthComplete(phoneNum1, AuthKind.findPwd).orElse(null);
        // then
        assertThat(target).isNull();
    }

    @Test
    public void findAuthComplete_인증된거있음() {
        // given
        AuthNumber authNumber = Creator.createAuthNumber(phoneNum1, authNum, AuthKind.findPwd, LocalDateTime.now());
        authNumberRepository.save(authNumber);
        em.flush();
        em.clear();
        // when
        AuthNumber target = authNumberRepository.findAuthComplete(phoneNum1, AuthKind.findPwd).orElse(null);
        // then
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(authNumber.getId());
    }

    @Test
    public void deleteByPhoneNumAndAuthKind() {
        // given
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        em.flush();
        em.clear();
        // when
        authNumberRepository.deleteByPhoneNumAndAuthKind(authNumber1.getPhoneNum(), AuthKind.signup);
        // then
        AuthNumber result = authNumberRepository.findById(authNumber1.getId()).orElse(null);
        assertThat(result).isNull();
    }

    @Test
    public void findByPhoneNumAndAuthNumAndAuthKindAndUserId() {
        // given
        em.persist(user);
        AuthNumber authNumber = AuthNumber.builder()
                .phoneNum(phoneNum1)
                .authNum(authNum)
                .authKind(AuthKind.updatePhoneNum)
                .userId(user.getId())
                .build();
        authNumberRepository.save(authNumber);
        authNumberRepository.save(authNumber1);
        authNumberRepository.save(authNumber2);
        authNumberRepository.save(authNumber3);
        authNumberRepository.save(authNumber4);
        em.flush();
        em.clear();
        // when
        AuthNumber target = authNumberRepository.findByPhoneNumAndAuthNumAndAuthKindAndUserId(phoneNum1, authNum, authNumber.getAuthKind(), user.getId())
                .orElse(null);
        // then
        assertThat(target).isNotNull();
        assertThat(target.getId()).isEqualTo(authNumber.getId());
    }


}
