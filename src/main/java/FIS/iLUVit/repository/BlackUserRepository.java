package FIS.iLUVit.repository;

import FIS.iLUVit.domain.BlackUser;
import FIS.iLUVit.domain.enumtype.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface BlackUserRepository extends JpaRepository<BlackUser, Long> {

    /**
     * 해당 로그인아이디로 블랙유저를 조회합니다
     */
    Optional<BlackUser> findByLoginId(String loginId);

    /**
     * 해당 유저아이디로 블랙유저를 조회합니다
     */
    Optional<BlackUser> findByUserId(Long userId);

    /**
     * 해당 닉네임으로 블랙유저를 조회합니다
     */
    Optional<BlackUser> findByNickName(String nickName);

    /**
     * 해당 핸드폰 번호와 유저 상태로 블랙 유저를 조회합니다
     */
    Optional<BlackUser> findByPhoneNumberAndUserStatus(String phoneNumber, UserStatus userStatus);

    /**
     * 해당 핸드폰 번호를 가지고 유저 상태가 영구정지/관리자에 의한 이용제한/신고누적 3회 이용제한인 블랙 유저를 조회합니다
     */
    @Query("SELECT blackUser FROM BlackUser blackUser " +
            "WHERE blackUser.phoneNumber = :phoneNumber AND " +
            "(blackUser.userStatus = 'SUSPENDED' OR blackUser.userStatus = 'RESTRICTED_ADMIN' OR blackUser.userStatus = 'RESTRICTED_REPORT') ")
    Optional<BlackUser> findRestrictedByPhoneNumber(String phoneNumber);

    /**
     * 해당 로그인 아이디를 가지고 유저 상태가 영구정지/관리자에 의한 이용제한/신고누적 3회 이용제한인 블랙 유저를 조회합니다
     */
    @Query("SELECT blackUser FROM BlackUser blackUser " +
            "WHERE blackUser.loginId = :loginId AND " +
            "(blackUser.userStatus = 'SUSPENDED' OR blackUser.userStatus = 'RESTRICTED_ADMIN' OR blackUser.userStatus = 'RESTRICTED_REPORT') ")
    Optional<BlackUser> findRestrictedByLoginId(String loginId);
}
