package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExpoTokenRepository extends JpaRepository<ExpoToken, Long> {

    /**
     * 해당 유저와 active 상태로 expoToken 리스트를 조회합니다
     */
    List<ExpoToken> findByUserAndActive(User user, Boolean active);

    /**
     * 해당 유저가 가진 토큰을 삭제합니다
     */
    void deleteByTokenAndUser(String token, User user);

    /**
     * 해당 토큰과 유저로 expoToken을 조회합니다
     */
    Optional<ExpoToken> findByTokenAndUser(String token, User user);

    /**
     * 특정 컬렉션 내에 있는 expoToken과 일치하는 expoToken을 삭제합니다
     */
    void deleteByTokenIn(Collection<String> tokens);

    /**
     * 해당 유저의 모든 expoToken을 삭제합니다
     */
    void deleteAllByUser(User user);

    /**
     * 디바이스 id와 일치하는 엑스포 토큰을 비활성화한다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ExpoToken expoToken SET expoToken.active = false WHERE expoToken.deviceId = :deviceId")
    void updateExpoTokenDeactivated(String deviceId);

    /**
     * 디바이스 id 와 활성화 여부와 일치하는 엑스포 토큰을 삭제한다
     */
    void deleteByDeviceIdAndActive(String deviceId, boolean active);
}
