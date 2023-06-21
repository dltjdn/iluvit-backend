package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {

    /*
        유저 id와 시설 id를 파라미터로 받아서 부모와 시설로 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "join c.parent p " +
            "join c.center ct " +
            "where p.id = :userId " +
            "and ct.id = :centerId")
    List<Child> findByParentAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        유저 id와 아이 id를 파라미터로 받아서 센터가 있는 ID 및 부모로 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParentWithCenter(@Param("userId") Long userId, @Param("childId") Long childId);

    /*
        유저 id와 아이 id를 파라미터로 받아서 아이디와 부모로 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParent(@Param("userId") Long userId, @Param("childId") Long childId);

    /*
        유저 id를 파라미터로 받아서 센터가 있는 사용자 id로 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.parent.id =:userId")
    List<Child> findByUserWithCenter(@Param("userId") Long userId);

    /*
        부모로 조회합니다.
     */
    List<Child> findByParent(Parent parent);


    /*
        시설로 조회합니다.
     */
    List<Child> findByCenter(Center center);


}
