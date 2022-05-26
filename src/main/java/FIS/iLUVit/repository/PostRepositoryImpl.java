package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.controller.dto.QGetPostResponsePreview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Set;

import static FIS.iLUVit.domain.QBoard.board;
import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

@AllArgsConstructor
public class PostRepositoryImpl extends PostQueryMethod implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<GetPostResponsePreview> findWithBoardAndCenter(Set<Long> centerIds, String keyword, Pageable pageable) {
        List<GetPostResponsePreview> posts = jpaQueryFactory.select(new QGetPostResponsePreview(post))
                .from(post)
                .join(post.board, board).fetchJoin()
                .join(board.center, center).fetchJoin()
                .where((centerIdIn(centerIds).or(center.id.isNull()))
                        .and(keywordContains(keyword)))
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
