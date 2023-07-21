package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.invoke.VarHandle.AccessMode.SET;
import static org.hibernate.cfg.beanvalidation.GroupsPerOperation.Operation.UPDATE;

public interface ExpoTokenRepository extends JpaRepository<ExpoToken, Long> {

    /*
        사용자로 expo 토큰 리스트를 조회합니다.
     */
    List<ExpoToken> findByUserAndActive(User user, Boolean active);

    /*
        토큰과 사용자로 Expo 토큰을 삭제합니다.
     */
    void deleteByTokenAndUser(String token, User user);

    /*
        토큰과 사용자로 expo 토큰을 조회합니다.
     */
    Optional<ExpoToken> findByTokenAndUser(String token, User user);

    /*
        토큰 인으로 expo 토큰을 삭제합니다.
     */
    void deleteByTokenIn(Collection<String> tokens);

    /*
        사용자로 모든 expo 토큰을 삭제합니다.
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
