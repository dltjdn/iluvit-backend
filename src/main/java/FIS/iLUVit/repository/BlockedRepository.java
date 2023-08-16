package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Blocked;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BlockedRepository extends JpaRepository<Blocked, Long> {
    
     /**
     * 해당 차단한 유저로 차단관계 리스트를 조회합니다
     */
    List<Blocked> findByBlockingUser(User blockingUser);

    /**
     * 해당 차단한 유저와 차단당한 유저로 차단관계를 조회합니다
     */
    Optional<Blocked> findByBlockingUserAndBlockedUser(User blockingUser, User blockedUser);

}
