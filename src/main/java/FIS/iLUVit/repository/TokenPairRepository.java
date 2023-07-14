package FIS.iLUVit.repository;

import FIS.iLUVit.domain.TokenPair;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    /**
     * 유저 id로 TokenPair를 조회합니다
     */
    Optional<TokenPair> findByUserId(@Param("userId") Long userId);

    /**
     * 해당 유저의 TokenPair를 조회합니다
     */
    Optional<TokenPair> findByUser(User user);

}

