package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.dto.post.PostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.List;

public interface PostRepositoryCustom {

    /*
        시설 id가 주어진 시설 id 리스트에 속하거나 null인지를 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    Slice<Post> findInCenterByKeyword(List<Center> centers, String keyword, Pageable pageable);

    /*
        시설 id가 주어진 시설 id와 동일한지 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    Slice<Post> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable);

    /*
        시설 id가 주어진 시설 id 값과 같은지 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    Slice<Post> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable);

    /*
        시설 id가 주어진 시설 id와 같고 게시글 엔티티의 하트 개수가 주어진 하트 개수보다 크거나 같은지 확인하여 게시글 미리보기 DTO를 불러옵니다.
     */
    Slice<Post> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable);
}
