package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.controller.dto.QGetPostResponsePreview;
import FIS.iLUVit.domain.enumtype.Auth;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static FIS.iLUVit.domain.QBoard.board;
import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

@AllArgsConstructor
public class PostRepositoryImpl extends PostQueryMethod implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<GetPostResponsePreview> findInCenterByKeyword(Collection<Long> centerIds, String keyword, Pageable pageable) {
        List<GetPostResponsePreview> posts = jpaQueryFactory.select(new QGetPostResponsePreview(post))
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
    public Slice<GetPostResponsePreview> findByCenterAndKeyword(Long centerId, String keyword, Pageable pageable) {
        List<GetPostResponsePreview> posts = jpaQueryFactory.select(new QGetPostResponsePreview(post))
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
    public Slice<GetPostResponsePreview> findByBoardAndKeyword(Long boardId, String keyword, Pageable pageable) {
        List<GetPostResponsePreview> posts = jpaQueryFactory.select(new QGetPostResponsePreview(post))
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
    public Slice<GetPostResponsePreview> findHotPosts(Long centerId, Integer heartCnt, Pageable pageable) {
        List<GetPostResponsePreview> posts = jpaQueryFactory.select(new QGetPostResponsePreview(post))
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
