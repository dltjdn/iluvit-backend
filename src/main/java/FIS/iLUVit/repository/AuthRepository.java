package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthNumber, Long> {

    @Modifying
    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
    void deleteExpiredNumber(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind")
    Optional<AuthNumber> findOverlap(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKind(String phoneNum, String authNum, AuthKind authKind);

    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind and a.authTime is not null")
    Optional<AuthNumber> findAuthComplete(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    @Modifying
    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
    void deleteByPhoneNumAndAuthKind(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    @Query("select a " +
            "from AuthNumber a " +
            "where a.phoneNum =:pNum " +
            "and a.authKind =:kind " +
            "and a.authNum =:aNum " +
            "and a.userId =:id")
    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKindAndUserId(
            @Param("pNum") String pNum, @Param("aNum") String aNum,@Param("kind") AuthKind kind,@Param("id") Long id);
}
