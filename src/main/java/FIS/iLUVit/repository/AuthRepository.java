package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthNumber, Long> {

    /*
        인증 전화번호와 인증종류별로 인증번호를 삭제합니다.
    */
//    @Modifying
//    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
//    void deleteExpiredNumber(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    /**
        작성날짜: 2023/07/10 2:46 PM
        작성자: 이서우
        작성내용: 해당 전화 번호와 인증 종류에 대한 인증 정보를 삭제 합니다.
    */
    void deleteByPhoneNumAndAuthKind(String phoneNum, AuthKind authKind);

    /*
        인증 전화번호와 인증종류별로 인증번호를 조회합니다.
    */
//    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind")
//    Optional<AuthNumber> findOverlap(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);
    /**
        작성날짜: 2023/07/10 2:52 PM
        작성자: 이서우
        작성내용: 해당 전화 번호와 인증 종류에 대한 인증 정보를 조회합니다
    */
    Optional<AuthNumber> findByPhoneNumAndAuthKind(String phoneNum, AuthKind authkind);

    /**
     작성날짜: 2023/07/10 2:59 PM
     작성자: 이서우
     작성내용: 해당 전화 번호와 인증 종류, 인증번호에 대한 인증 정보를 조회합니다
     */
    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKind(String phoneNum, String authNum, AuthKind authKind);

    /*
        인증 시간이 null이 아닌것들 중에 전화번호와 인증종류별로 인증번호를 조회합니다.
    */
//    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind and a.authTime is not null")
//    Optional<AuthNumber> findAuthComplete(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    /**
        작성날짜: 2023/07/10 3:05 PM
        작성자: 이서우
        작성내용: 인증이 완료된 것들 중 해당 전화 번호화 인증 종류에 대한 인증 정보를 조회합니다
    */
    Optional<AuthNumber> findByPhoneNumAndAuthKindAndAuthTimeNotNull(String phoneNum, AuthKind authKind);

    /*
        인증 전화번호와 인증종류별로 인증번호를 삭제합니다.
    */
//    @Modifying
//    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
//    void deleteByPhoneNumAndAuthKind(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    /*
        인증 전화번호와 인증종류와 인증번호와 사용자 id별로 인증번호를 조회합니다.
//    */
//    @Query("select a " +
//            "from AuthNumber a " +
//            "where a.phoneNum =:pNum " +
//            "and a.authKind =:kind " +
//            "and a.authNum =:aNum " +
//            "and a.userId =:id")
//    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKindAndUserId(
//            @Param("pNum") String pNum, @Param("aNum") String aNum,@Param("kind") AuthKind kind,@Param("id") Long id);

    /**
        작성날짜: 2023/07/10 2:59 PM
        작성자: 이서우
        작성내용: 해당 전화 번호와 인증 종류, 인증번호, 유저에 대한 인증 정보를 조회합니다
    */
    Optional<AuthNumber> findByPhoneNumAndAuthKindAndAuthNumAndUserId(String phoneNum, AuthKind authKind, String authNum, Long userId);
}
