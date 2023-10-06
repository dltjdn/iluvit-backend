package FIS.iLUVit.domain.tokenpair.repository;

import FIS.iLUVit.domain.tokenpair.domain.TokenPair;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    /**
     * 해당 유저의 TokenPair를 조회합니다
     */
    Optional<TokenPair> findByUser(User user);

}

