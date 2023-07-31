package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>{

    /**
     * 해당 학부모로 Pageable하여 리뷰를 조회합니다
     */
    Slice<Review> findByParent(Parent parent, Pageable pageable);

    /**
     * 해당 시설로 생성일자 기준 내림차순하여 리뷰를 조회합니다
     */
    Slice<Review> findByCenterOrderByCreatedDate(Center center, Pageable pageable);

    /**
     * 해당 학부모와 시설로 리뷰를 조회합니다
     */
    Optional<Review> findByParentAndCenter(Parent parent, Center center);

    /**
     * 해당 시설로 리뷰를 조회합니다
     */
    List<Review> findByCenter(Center center);

}
