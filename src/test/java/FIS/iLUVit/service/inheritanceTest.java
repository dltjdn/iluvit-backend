package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.repository.iluvit.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class inheritanceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("쿼리 어떻게?")
    public void 쿼리어떻게(){
        userRepository.findAll();
        List<User> byIdTest = userRepository.findByIdTest();
        byIdTest.forEach(user -> {
            if(user.getDtype().equals("Teacher")) {
                Teacher t = (Teacher) user;
                System.out.println("Teacher" + t.getId() + t.getApproval());
            }
            else {
                Parent t = (Parent) user;
                System.out.println("Parent" + t.getId());
            }
        });
    }
}
