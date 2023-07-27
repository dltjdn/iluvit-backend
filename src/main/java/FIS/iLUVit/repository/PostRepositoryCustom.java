package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.dto.post.PostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostRepositoryCustom {

    /**
     *  유저가 속한 센터 게시판 + 모두의 게시판(center == null)에서 해당 키워드를 포함하는 게시글들을 조회합니다
     */
    Slice<Post> findInCenterByKeyword(List<Center> centers, String keyword, Pageable pageable);

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 해당 키워드를 포함하는 게시글들을 조회합니다
     */
    Slice<Post> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable);

    /**
     * 해당 게시판에서 해당 키워드를 포함하는 게시글들을 모두 조회합니다
     */
    Slice<Post> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable);

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 좋아요 수가 일정개수 이상인 게시글들을 반환합니다
     */
    Slice<Post> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable);
}
