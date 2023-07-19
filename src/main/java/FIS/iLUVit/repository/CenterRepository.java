package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    /*
        부모님 id별로 시설을 조회합니다.
     */
    @Query("select ct from Child c join c.center ct join c.parent p where p.id = :userId")
    List<Center> findByUser(@Param("userId") Long userId);

    /*
        시설 id별로 시설을 조회합니다.
     */
    @Query("select distinct c " +
            "from Center c " +
            "where c.id =:centerId")
    Optional<Center> findByIdWithTeacher(@Param("centerId") Long centerId);

    /*
        시승인이 ACCEPT이고 서명이 되어있다면 id별로 시설을 조회합니다.
     */
    @Query("select distinct c " +
            "from Center c " +
            "where c.id =:centerId " +
            "and c.signed = true")
    Optional<Center> findByIdAndSigned(@Param("centerId") Long center_id);


}