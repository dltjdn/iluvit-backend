package FIS.iLUVit.domain.post.repository;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostRepositoryCustom {

    /**
     * 게시판의 게시물 중 유저가 차단하지 않은 게시물을 조회합니다
     */
    List<Post> findByBoardAndUserIdNotIn(Board board, List<Long> blockedUserIds);

    /**
     * 일정 좋아요 개수 이상의 게시물을 조회한다 ( 센터가 null이면 모든 게시물, 센터가 null이 아니면 해당 센터의 게시물 )
     */
    List<Post> findHotPostsByHeartCnt(int heartCnt, Long centerId, List<Long> blockedUserIds);

    /**
     *  유저가 속한 센터 게시판 + 모두의 게시판(center == null)에서 해당 키워드를 포함하는 게시글들을 조회합니다
     */
    Slice<Post> findInCenterByKeyword(List<Center> centers, String keyword, List<Long> blockedUserIds, Pageable pageable);

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 해당 키워드를 포함하는 게시글들을 조회합니다
     */
    Slice<Post> findByCenterAndKeyword(Long centerId, String keyword, List<Long> blockedUserIds, Pageable pageable);

    /**
     * 해당 게시판에서 해당 키워드를 포함하는 게시글들을 모두 조회합니다
     */
    Slice<Post> findByBoardAndKeyword(Long boardId, String keyword, List<Long> blockedUserIds, Pageable pageable);

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 좋아요 수가 일정개수 이상인 게시글들을 반환합니다
     */
    Slice<Post> findHotPosts(Long centerId, Integer heartCnt, List<Long> blockedUserIds, Pageable pageable);
}
