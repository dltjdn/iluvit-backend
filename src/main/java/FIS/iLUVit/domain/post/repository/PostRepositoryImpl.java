package FIS.iLUVit.domain.post.repository;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.post.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import java.util.List;

import static FIS.iLUVit.domain.board.domain.QBoard.board;
import static FIS.iLUVit.domain.center.domain.QCenter.center;
import static FIS.iLUVit.domain.post.domain.QPost.post;

@AllArgsConstructor
public class PostRepositoryImpl extends PostQueryMethod implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findByBoardAndUserIdNotIn(Board findBoard, List<Long> blockedUserIds) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(board.eq(findBoard), post.user.id.notIn(blockedUserIds))
                .orderBy(post.createdDate.desc())
                .fetch();

        return posts;
    }

    @Override
    public List<Post> findHotPostsByHeartCnt(int heartCnt, Long centerId, List<Long> blockedUserIds) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), post.heartCnt.goe(heartCnt), post.user.id.notIn(blockedUserIds))
                .orderBy(post.createdDate.desc())
                .fetch();

        return posts;
    }


    /**
     *  유저가 속한 시설 게시판들 + 모두의 게시판(center == null)에서 해당 키워드를 포함하는 게시글들을 조회합니다
     *  ( 게시글 생성 날짜 내림차순 정렬 )
     */

    @Override
    public Slice<Post> findInCenterByKeyword(List<Center> centers, String keyword,  List<Long> blockedUserIds,  Pageable pageable) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(center.in(centers).or(center.isNull()), keywordContains(keyword), post.user.id.notIn(blockedUserIds))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return convertToSlice(posts, pageable);
    }

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 해당 키워드를 포함하는 게시글들을 조회합니다
     */
    @Override
    public Slice<Post> findByCenterAndKeyword(Long centerId, String keyword, List<Long> blockedUserIds, Pageable pageable) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), keywordContains(keyword), post.user.id.notIn(blockedUserIds))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return convertToSlice(posts, pageable);
    }

   /**
    * 해당 게시판에서 해당 키워드를 포함하는 게시글들을 모두 조회합니다
    */
    @Override
    public Slice<Post> findByBoardAndKeyword(Long boardId, String keyword, List<Long> blockedUserIds, Pageable pageable) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .where(board.id.eq(boardId), keywordContains(keyword), post.user.id.notIn(blockedUserIds))
                .orderBy(post.postUpdateDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return convertToSlice(posts, pageable);
    }

    /**
     * 해당 시설 게시판 or 모두의 게시판에서 좋아요 수가 일정개수 이상인 게시글들을 반환합니다
     */
    @Override
    public Slice<Post> findHotPosts(Long centerId, Integer heartCnt, List<Long> blockedUserIds, Pageable pageable) {
        List<Post> posts = jpaQueryFactory.select(post)
                .from(post)
                .join(post.board, board).fetchJoin()
                .leftJoin(board.center, center).fetchJoin()
                .where(centerIdEq(centerId), post.heartCnt.goe(heartCnt), post.user.id.notIn(blockedUserIds))
                .orderBy(post.postCreateDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return convertToSlice(posts, pageable);
    }

    /**
     *  List -> SliceImp
     */
    @NotNull
    private static SliceImpl<Post> convertToSlice(List<Post> posts, Pageable pageable) {
        boolean hasNext = posts.size() > pageable.getPageSize();

        if (hasNext) {
            posts.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }


}
