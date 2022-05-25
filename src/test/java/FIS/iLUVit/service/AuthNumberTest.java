package FIS.iLUVit.service;



import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
public class AuthNumberTest {

    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Test
    void test() throws InterruptedException {
        LocalDateTime a = LocalDateTime.now();
        Thread.sleep(1000);
        LocalDateTime b = LocalDateTime.now();
        Duration duration = Duration.between(a, b);
        System.out.println(duration.getSeconds());
    }

    @Test
    void testUnique() {
        Parent parent = Parent.createParent("sdaasd", "qwe", encoder.encode("asd"), "asd", false, "qwe@qwe.com", "qwe", new Theme(), 5, Auth.PARENT);
        em.persist(parent);
        em.flush();
        em.clear();
        User byLoginId = userRepository.findByLoginId(parent.getLoginId()).orElse(null);
        System.out.println("byLoginId = " + byLoginId.getName());

    }
}
