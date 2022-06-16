package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Set;

public interface PostRepositoryCustom {

    Slice<GetPostResponsePreview> findWithBoardAndCenter(Set<Long> centerIds, String keyword, Pageable pageable);
}
