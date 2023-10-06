package FIS.iLUVit.domain.blocked.repository;

import FIS.iLUVit.domain.blocked.domain.Blocked;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockedRepository extends JpaRepository<Blocked, Long> {

    /**
     * 해당 차단한 유저와 차단당한 유저로 차단관계를 조회합니다
     */
    Optional<Blocked> findByBlockingUserAndBlockedUser(User blockingUser, User blockedUser);

    /**
     * 해당 차단한 유저로 차단관계 리스트를 조회합니다
     */
    List<Blocked> findByBlockingUser(User blockingUser);

}