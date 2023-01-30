package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.PostPreviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;

public interface PostRepositoryCustom {

    Slice<PostPreviewDto> findInCenterByKeyword(Collection<Long> centerIds, String keyword, Pageable pageable);

    Slice<PostPreviewDto> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable);

    Slice<PostPreviewDto> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable);

    Slice<PostPreviewDto> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable);
}
