package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.PostPreviewResponse;
import FIS.iLUVit.controller.dto.QPostPreviewResponse;
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

    @Override
    public Slice<PostPreviewResponse> findInCenterByKeyword(Collection<Long> centerIds, String keyword, Pageable pageable) {
        List<PostPreviewResponse> posts = jpaQueryFactory.select(new QPostPreviewResponse(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdIn(centerIds).or(center.id.isNull()), keywordContains(keyword))
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

    @Override
    public Slice<PostPreviewResponse> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable) {
        List<PostPreviewResponse> posts = jpaQueryFactory.select(new QPostPreviewResponse(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), keywordContains(keyword))
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

    @Override
    public Slice<PostPreviewResponse> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable) {
        List<PostPreviewResponse> posts = jpaQueryFactory.select(new QPostPreviewResponse(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .where(board.id.eq(boardId), keywordContains(keyword))
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

    @Override
    public Slice<PostPreviewResponse> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable) {
        List<PostPreviewResponse> posts = jpaQueryFactory.select(new QPostPreviewResponse(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), post.heartCnt.goe(heartCnt))
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
