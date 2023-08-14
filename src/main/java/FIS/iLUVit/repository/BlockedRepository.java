package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Blocked;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockedRepository extends JpaRepository<Blocked, Long> {
    List<Blocked> findByBlockingUser(User blockingUser);
}
