package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Blocked;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.dto.post.QPostPreviewDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Collection;
import java.util.List;

import static FIS.iLUVit.domain.QBoard.board;
import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

@AllArgsConstructor
public class PostRepositoryImpl extends PostQueryMethod implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /*
        시설 id가 주어진 시설 id 리스트에 속하거나 null인지를 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    @Override
    public Slice<PostPreviewDto> findInCenterByKeyword(Collection<Long> centerIds, String keyword, List<User> blockedUsers, Pageable pageable) {
        List<PostPreviewDto> posts = jpaQueryFactory.select(new QPostPreviewDto(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdIn(centerIds).or(center.id.isNull()), keywordContains(keyword), post.user.notIn(blockedUsers))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        boolean hasNext = false;

        if(posts.size() > pageable.getPageSize()){
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(posts, pageable, hasNext);
    }

    /*
        시설 id가 주어진 시설 id와 동일한지 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    @Override
    public Slice<PostPreviewDto> findByCenterAndKeyword(Long centerId, String keyword, List<User> blockedUsers, Pageable pageable) {
        List<PostPreviewDto> posts = jpaQueryFactory.select(new QPostPreviewDto(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), keywordContains(keyword), post.user.notIn(blockedUsers))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if(posts.size() > pageable.getPageSize()){
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(posts, pageable, hasNext);
    }

    /*
        시설 id가 주어진 시설 id 값과 같은지 확인하고 keywordContains 메서드를 호출하여 키워드 검색을 적용하여 게시글 미리보기 DTO 객체를 불러옵니다.
     */
    @Override
    public Slice<PostPreviewDto> findByBoardAndKeyword(Long boardId, String keyword, List<User> blockedUsers, Pageable pageable) {
        List<PostPreviewDto> posts = jpaQueryFactory.select(new QPostPreviewDto(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .where(board.id.eq(boardId), keywordContains(keyword), post.user.notIn(blockedUsers))
                .orderBy(post.postUpdateDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if(posts.size() > pageable.getPageSize()){
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(posts, pageable, hasNext);
    }

    /*
        시설 id가 주어진 시설 id와 같고 게시글 엔티티의 하트 개수가 주어진 하트 개수보다 크거나 같은지 확인하여 게시글 미리보기 DTO를 불러옵니다.
     */
    @Override
    public Slice<PostPreviewDto> findHotPosts(Long centerId, Integer heartCnt, List<User> blockedUsers, Pageable pageable) {
        List<PostPreviewDto> posts = jpaQueryFactory.select(new QPostPreviewDto(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), post.heartCnt.goe(heartCnt), post.user.notIn(blockedUsers))
                .orderBy(post.postCreateDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;

        if(posts.size() > pageable.getPageSize()){
            posts.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
