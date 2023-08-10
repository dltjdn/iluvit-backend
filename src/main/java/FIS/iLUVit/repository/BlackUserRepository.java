package FIS.iLUVit.repository;

import FIS.iLUVit.domain.BlackUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackUserRepository extends JpaRepository<BlackUser, Long> {

    /**
     * 해당 로그인아이디로 블랙유저를 조회합니다
     */
    Optional<BlackUser> findByLoginId(String loginId);

    Optional<BlackUser> findByUserId(Long userId);
}
