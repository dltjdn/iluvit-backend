package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthNumber, Long> {

    /*
        전화번호와 인증 종류를 파라미터로 받아서 예전 인증번호 관련 정보를 지운다.
     */
    @Modifying
    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
    void deleteExpiredNumber(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    /*
        전화번호와 인증 종류를 파라미터로 받아서 겹치는 인증번호를 찾는다.
    */
    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind")
    Optional<AuthNumber> findOverlap(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    /*
        전화번호와 인증번호와 인증 종류로 찾는다.
    */
    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKind(String phoneNum, String authNum, AuthKind authKind);

    /*
        전화번호와 인증 종류를 파라미터로 받아서 인증 완료를 조회한다.
    */
    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind and a.authTime is not null")
    Optional<AuthNumber> findAuthComplete(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    /*
        전화번호와 인증 종류를 파라미터로 받아서 삭제한다.
    */
    @Modifying
    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
    void deleteByPhoneNumAndAuthKind(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    /*
        전화번호와 인증번호와 인증 종류와 유저 id를 파라미터로 받아서 조회한다.
    */
    @Query("select a " +
            "from AuthNumber a " +
            "where a.phoneNum =:pNum " +
            "and a.authKind =:kind " +
            "and a.authNum =:aNum " +
            "and a.userId =:id")
    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKindAndUserId(
            @Param("pNum") String pNum, @Param("aNum") String aNum,@Param("kind") AuthKind kind,@Param("id") Long id);
}
