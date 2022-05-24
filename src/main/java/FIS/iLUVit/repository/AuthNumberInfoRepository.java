package FIS.iLUVit.repository;

import FIS.iLUVit.domain.AuthNumberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthNumberInfoRepository extends JpaRepository<AuthNumberInfo, Long> {

    @Query("delete from AuthNumberInfo a where a.phoneNum =:pNum")
    void deleteExpiredNumber(@Param("pNum") String toNumber);

    @Query("select a from AuthNumberInfo a where a.phoneNum = :pNum order by a.createdDate")
    List<AuthNumberInfo> findOverlap(@Param("pNum") String toNumber);

    Optional<AuthNumberInfo> findByPhoneNumAndAuthNum(@Param("pNum") String phoneNum, @Param("authNum") String authNum);

//    List<AuthNumberInfo> findByPhoneNum
}
