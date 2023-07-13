package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    /**
     * 해당 시설의 게시판 리스트를 조회합니다
     */
    List<Board> findByCenter(Center center);

    /**
     * 시설에 속해있지 않은 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterIsNull();

    /**
     * 시설에 속해 있지 않은 게시판 중 해당 이름과 일치하는 게시판을 조회합니다
     */
    Optional<Board> findByCenterIsNullAndName(String name);

    /**
     * 해당 시설에서 해당 이름을 가진 게시판을 조회합니다
     */
    Optional<Board> findByCenterAndName(Center center, String name);

    /**
     * 해당 시설에서 디폴트인 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterAndIsDefaultTrue(Center center);

    /**
     * 시설에 속해있지 않은 게시판 중 디폴트인 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterIsNullAndIsDefaultTrue();

}
