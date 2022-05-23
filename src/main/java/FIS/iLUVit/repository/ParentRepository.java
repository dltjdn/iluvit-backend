package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    @Query("select p from Parent p join fetch p.children where p.id = :id")
    Optional<Parent> findWithChildren(@Param("id") Long id);

    Optional<Parent> findByNickName(String nickname);
}
