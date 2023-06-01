package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExpoTokenRepository extends JpaRepository<ExpoToken, Long> {

    /*
        사용자로 expo 토큰을 조회합니다.
     */
    List<ExpoToken> findByUser(User user);

    /*
        토큰으로 expo 토큰을 조회합니다.
     */
    Optional<ExpoToken> findByToken(String token);

    /*
        토큰 및 사용자별로 삭제합니다.
     */
    void deleteByTokenAndUser(String token, User user);

    /*
        토큰 및 사용자로 expo 토큰을 조회합니다.
     */
    Optional<ExpoToken> findByTokenAndUser(String token, User user);

    /*
        토큰 인으로 삭제합니다.
     */
    void deleteByTokenIn(Collection<String> tokens);

    /*
        사용자별로 모두 삭제합니다.
     */
    void deleteAllByUser(User user);
}
