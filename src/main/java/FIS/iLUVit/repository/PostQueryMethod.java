package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Collection;
import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPost.post;

public class PostQueryMethod {

    /**
     * 키워드가 null, 빈 문자열 ->  null 반환
     * 키워드 not null -> 제목 또는 내용에 주어진 키워드가 포함되는지를 검사하는 조건을 생성하여 반환
     */
    protected BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.equals("")) ? null : post.title.contains(keyword).or(post.content.contains(keyword));
    }


    /**
     * 시설 id가 not null -> 해당 시설 id와 일치하는 조건을 생성하여 반환
     * 시설 id가 null -> null인지를 검사하는 조건을 생성하여 반환
     */
    protected BooleanExpression centerIdEq(Long centerId) {
        return centerId != null ? center.id.eq(centerId) : center.id.isNull();
    }
}
