package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Set;

public interface PostRepositoryCustom {

    Slice<GetPostResponsePreview> findWithBoardAndCenter(Set<Long> centerIds, String keyword, Pageable pageable);

    Slice<GetPostResponsePreview> findWithCenter(Long centerId, String keyword, Auth auth, Long userId, Pageable pageable);

    Slice<GetPostResponsePreview> findWithBoard(Long boardId, String keyword, Pageable pageable);
}
