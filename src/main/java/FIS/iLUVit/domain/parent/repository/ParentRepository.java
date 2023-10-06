package FIS.iLUVit.domain.parent.repository;

import FIS.iLUVit.domain.parent.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * 닉네임으로 부모를 조회합니다.
     */
    Optional<Parent> findByNickName(String nickname);
}
