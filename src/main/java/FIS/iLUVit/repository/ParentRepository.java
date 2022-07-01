package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children c " +
            "join fetch c.center " +
            "where p.id = :id")
    Optional<Parent> findWithChildren(@Param("id") Long id);

    Optional<Parent> findByNickName(String nickname);

    @Query("select distinct parent from Parent parent " +
            "left join fetch parent.participations as participation " +
            "left join fetch participation.ptDate as ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "where parent.id = :userId")
    Optional<Parent> findByIdAndFetchPresentation(@Param("userId") Long userId);

    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children " +
            "where p.id =:userId")
    Parent findByIdWithChild(@Param("userId") Long userId);

    @Query("select p " +
            "from Parent p " +
            "left join fetch p.prefers pp " +
            "join fetch pp.center " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithPreferWithCenter(Long userId);
}
