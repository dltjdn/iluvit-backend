package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {

    /**
     * 해당 학부모 id로 아이 리스트를 조회합니다
     */
    List<Child> findByParentId(@Param("parentId") Long parentId);

    /*
        부모 id와 시설 id로 아이 리스트를 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "join c.parent p " +
            "join c.center ct " +
            "where p.id = :userId " +
            "and ct.id = :centerId")
    List<Child> findByParentAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        시설에 있는 아이 id와 부모 id로 아이를 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParentWithCenter(@Param("userId") Long userId, @Param("childId") Long childId);

    /*
        아이 id와 부모 id로 아이를 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParent(@Param("userId") Long userId, @Param("childId") Long childId);

    /*
        시설에 있는 부모 id로 아이 리스트를 조회합니다.
     */
    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.parent.id =:userId")
    List<Child> findByUserWithCenter(@Param("userId") Long userId);

    /*
        부모로 아이 리스트를 조회합니다.
     */
    List<Child> findByParent(Parent parent);

    /*
        센터로 아이 리스트를 조회합니다.
     */
    List<Child> findByCenter(Center center);

}
