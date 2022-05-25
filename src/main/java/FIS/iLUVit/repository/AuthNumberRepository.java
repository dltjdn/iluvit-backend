package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.enumtype.AuthKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthNumberRepository extends JpaRepository<AuthNumber, Long> {

    @Modifying
    @Query("delete from AuthNumber a where a.phoneNum =:pNum and a.authKind =:kind")
    void deleteExpiredNumber(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind order by a.createdDate")
    Optional<AuthNumber> findOverlap(@Param("pNum") String toNumber, @Param("kind")AuthKind authKind);

    Optional<AuthNumber> findByPhoneNumAndAuthNumAndAuthKind(String phoneNum, String authNum, AuthKind authKind);

    @Query("select a from AuthNumber a where a.phoneNum = :pNum and a.authKind =:kind and a.authTime is not null")
    Optional<AuthNumber> findAuthComplete(@Param("pNum") String phoneNum, @Param("kind") AuthKind authKind);

    void deleteAllByPhoneNum(String phoneNum);
}
