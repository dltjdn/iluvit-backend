package FIS.iLUVit.repository;

import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByLoginId(String loginId);
}
