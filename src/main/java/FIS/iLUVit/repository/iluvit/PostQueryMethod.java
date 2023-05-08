package FIS.iLUVit.repository.iluvit;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Collection;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

public class PostQueryMethod {
    protected BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.equals("")) ? null : post.title.contains(keyword).or(post.content.contains(keyword));
    }

    protected BooleanExpression centerIdIn(Collection<Long> centerIds) {
        return centerIds != null ? center.id.in(centerIds) : null;
    }

    protected BooleanExpression centerIdEq(Long centerId) {
        return centerId != null ? center.id.eq(centerId) : center.id.isNull();
    }
}
