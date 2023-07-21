package FIS.iLUVit.repository;

import FIS.iLUVit.dto.participation.ParticipationDto;
import FIS.iLUVit.domain.Parent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * 닉네임으로 부모를 조회합니다.
     */
    Optional<Parent> findByNickName(String nickname);
}
