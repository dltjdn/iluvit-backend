package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.predicate;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    private User parent1;
    private User parent2;
    private User teacher1;
    private User teacher2;

    @BeforeEach
    public void init() {
        parent1 = Parent.builder()
                .phoneNumber("parent1")
                .loginId("parent1")
                .nickName("parent1")
                .build();
        parent2 = Parent.builder()
                .phoneNumber("parent2")
                .loginId("parent2")
                .nickName("parent2")
                .build();
        teacher1 = Teacher.builder()
                .phoneNumber("teacher1")
                .loginId("teacher1")
                .build();
        teacher2 = Teacher.builder()
                .phoneNumber("teacher2")
                .loginId("teacher2")
                .build();
    }

    @Test
    public void 이미가입된전화번호인지확인() {
        // given
        userRepository.save(parent1);
        userRepository.save(parent2);
        userRepository.save(teacher1);
        userRepository.save(teacher2);
        em.flush();
        em.clear();
        // when
        User findUser = userRepository.findByPhoneNumber(parent1.getPhoneNumber()).orElse(null);
        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(parent1.getId());
    }

    @Test
    public void 로그인아이디와휴대폰번호로유저조회() {
        // given
        userRepository.save(parent1);
        userRepository.save(parent2);
        userRepository.save(teacher1);
        userRepository.save(teacher2);
        em.flush();
        em.clear();
        // when
        User findUser = userRepository.findByLoginIdAndPhoneNumber(parent1.getLoginId(), parent1.getPhoneNumber())
                .orElse(null);
        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(parent1.getId());
    }

    @ParameterizedTest
    @MethodSource("duplicatedLoginIdOrNickName")
    public void 로그인아이디또는닉네임이같은사용자조회(String loginId, String nickName) {
        // given
        userRepository.save(parent1);
        userRepository.save(parent2);
        userRepository.save(teacher1);
        em.flush();
        em.clear();
        // when
        User result = userRepository.findByLoginIdOrNickName(loginId, nickName)
                .orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(parent1.getId());
    }

    private static Stream<Arguments> duplicatedLoginIdOrNickName() {
        return Stream.of(
                Arguments.of("parent1", "parent1"),
                Arguments.of("parent11", "parent1"),
                Arguments.of("parent1", "parent11")
        );
    }
}
