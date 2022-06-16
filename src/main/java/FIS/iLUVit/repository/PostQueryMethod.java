package FIS.iLUVit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Set;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

public class PostQueryMethod {
    protected BooleanExpression keywordContains(String keyword) {
        return keyword != null ? post.title.contains(keyword).or(post.content.contains(keyword)) : null;
    }

    protected BooleanExpression centerIdIn(Set<Long> centerIds) {
        return centerIds != null ? center.id.in(centerIds) : null;
    }
}
