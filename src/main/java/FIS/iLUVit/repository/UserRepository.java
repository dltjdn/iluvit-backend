package FIS.iLUVit.repository;

import FIS.iLUVit.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 해당 로그인 아이디로 유저를 조회합니다
     */
    Optional<User> findByLoginId(String loginId);

    /**
     * 해당 닉네임으로 유저를 조회합니다
     */
    Optional<User> findByNickName(String nickname);

    /**
     * 해당 로그인 아이디 또는 닉네임으로 유저를 조회합니다
     */
    Optional<User> findByLoginIdOrNickName(String loginId, String nickName);

    /**
     * 해당 전화번호로 유저를 조회합니다
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 해당 로그인 아이디와 전화번호로 유저를 조회합니다
     */
    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

}
