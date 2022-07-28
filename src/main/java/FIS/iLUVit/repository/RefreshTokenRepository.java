package FIS.iLUVit.repository;

import FIS.iLUVit.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshToken);

    @Query("select r " +
            "from RefreshToken r " +
            "join fetch r.user u " +
            "where u.id =:userId")
    Optional<RefreshToken> findByUserIdWithUser(@Param("userId") Long userId);

    @Query("select r " +
            "from RefreshToken r " +
            "where r.user.id =:userId")
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);
}
