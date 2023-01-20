package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.PostPreviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;

public interface PostRepositoryCustom {

    Slice<PostPreviewResponse> findInCenterByKeyword(Collection<Long> centerIds, String keyword, Pageable pageable);

    Slice<PostPreviewResponse> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable);

    Slice<PostPreviewResponse> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable);

    Slice<PostPreviewResponse> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable);
}
