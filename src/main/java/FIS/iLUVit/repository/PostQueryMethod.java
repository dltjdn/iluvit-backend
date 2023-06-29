package FIS.iLUVit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Collection;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

public class PostQueryMethod {

    /*
        키워드를 기반으로 조건을 생성합니다. (키워드가 null이거나 빈 문자열인 경우 null을 반환하며, 그렇지 않은 경우 제목 또는 내용에 주어진 키워드가 포함되는지를 검사하는 조건을 생성하여 반환함)
     */
    protected BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.equals("")) ? null : post.title.contains(keyword).or(post.content.contains(keyword));
    }

    /*
        센터 ID들을 기반으로 조건을 생성합니다. (센터 ID들이 null이 아닌 경우, 해당 센터 ID들에 대한 조건을 생성하여 반환하며, 센터 ID들이 null인 경우, null을 반환함)
     */
    protected BooleanExpression centerIdIn(Collection<Long> centerIds) {
        return centerIds != null ? center.id.in(centerIds) : null;
    }

    /*
        센터 ID를 기반으로 조건을 생성합니다. (센터 ID가 null이 아닌 경우 해당 센터 ID와 일치하는 조건을 생성하여 반환하고, 센터 ID가 null인 경우 null인지를 검사하는 조건을 생성하여 반환함)
     */
    protected BooleanExpression centerIdEq(Long centerId) {
        return centerId != null ? center.id.eq(centerId) : center.id.isNull();
    }
}
