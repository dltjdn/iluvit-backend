package FIS.iLUVit.repository;

import FIS.iLUVit.domain.TokenPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    /*
        공정토큰 사용자 id가 사용자 id와 같은 공정 토큰을 불러옵니다.
     */
    @Query("select r " +
            "from TokenPair r " +
            "join fetch r.user u " +
            "where u.id =:userId")
    Optional<TokenPair> findByUserIdWithUser(@Param("userId") Long userId);

    /*
        공정토큰 사용자 id가 사용자 id와 같은 공정 토큰을 불러옵니다.
     */
    @Query("select r " +
            "from TokenPair r " +
            "where r.user.id =:userId")
    Optional<TokenPair> findByUserId(@Param("userId") Long userId);
}

