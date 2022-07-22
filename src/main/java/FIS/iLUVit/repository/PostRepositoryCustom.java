package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.Set;

public interface PostRepositoryCustom {

    Slice<GetPostResponsePreview> findInCenterByKeyword(Collection<Long> centerIds, String keyword, Pageable pageable);

    Slice<GetPostResponsePreview> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable);

    Slice<GetPostResponsePreview> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable);

    Slice<GetPostResponsePreview> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable);
}
