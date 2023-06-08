package FIS.iLUVit.repository;

import FIS.iLUVit.domain.TokenPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    /*
        사용자 id를 파라미터로 받아서 사용자에 사용자 id로 공정토큰을 조회합니다.
     */
    @Query("select r " +
            "from TokenPair r " +
            "join fetch r.user u " +
            "where u.id =:userId")
    Optional<TokenPair> findByUserIdWithUser(@Param("userId") Long userId);

    /*
        사용자 id를 파라미터로 받아서 사용자 id로 공정토큰을 조회합니다.
     */
    @Query("select r " +
            "from TokenPair r " +
            "where r.user.id =:userId")
    Optional<TokenPair> findByUserId(@Param("userId") Long userId);
}

