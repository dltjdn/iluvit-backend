package FIS.iLUVit.repository;

import FIS.iLUVit.domain.TokenPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    @Query("select r " +
            "from TokenPair r " +
            "join fetch r.user u " +
            "where u.id =:userId")
    Optional<TokenPair> findByUserIdWithUser(@Param("userId") Long userId);

    @Query("select r " +
            "from TokenPair r " +
            "where r.user.id =:userId")
    Optional<TokenPair> findByUserId(@Param("userId") Long userId);
}

