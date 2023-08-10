package FIS.iLUVit.repository;

import FIS.iLUVit.domain.BlackUser;
import FIS.iLUVit.domain.enumtype.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlackUserRepository extends JpaRepository<BlackUser, Long> {

    /**
     * 해당 로그인아이디로 블랙유저를 조회합니다
     */
    Optional<BlackUser> findByLoginId(String loginId);

    Optional<BlackUser> findByUserId(Long userId);

    Optional<BlackUser> findByPhoneNumberAndUserStatus(String phoneNumber, UserStatus userStatus);

    @Query("SELECT blackUser FROM BlackUser blackUser " +
            "WHERE blackUser.phoneNumber = :phoneNumber AND " +
            "(blackUser.userStatus = 'SUSPENDED' OR blackUser.userStatus = 'RESTRICTED_ADMIN' OR blackUser.userStatus = 'RESTRICTED_REPORT') ")
    Optional<BlackUser> findRestrictedByPhoneNumber(String phoneNumber);
}
