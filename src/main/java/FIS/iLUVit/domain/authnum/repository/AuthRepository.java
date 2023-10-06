package FIS.iLUVit.domain.authnum.repository;

import FIS.iLUVit.domain.authnum.domain.AuthNumber;
import FIS.iLUVit.domain.authnum.domain.AuthKind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthNumber, Long> {

    /**
     * 해당 전화 번호와 인증 종류에 대한 인증 정보를 삭제 합니다.
     */
    void deleteByPhoneNumAndAuthKind(String phoneNum, AuthKind authKind);

    /**
     * 해당 전화 번호와 인증 종류에 대한 인증 정보를 조회합니다
     */
    Optional<AuthNumber> findByPhoneNumAndAuthKind(String phoneNum, AuthKind authkind);

    /**
     * 해당 전화 번호와 인증 종류, 인증번호에 대한 인증 정보를 조회합니다
     */
    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKind(String phoneNum, String authNum, AuthKind authKind);


    /**
     * 인증이 완료된 것들 중 해당 전화 번호화 인증 종류에 대한 인증 정보를 조회합니다
     */
    Optional<AuthNumber> findByPhoneNumAndAuthKindAndAuthTimeNotNull(String phoneNum, AuthKind authKind);

    /**
     * 해당 전화 번호와 인증 종류, 인증번호, 유저에 대한 인증 정보를 조회합니다
     */
    Optional<AuthNumber> findByPhoneNumAndAuthKindAndAuthNumAndUserId(String phoneNum, AuthKind authKind, String authNum, Long userId);
}
