package FIS.iLUVit.repository;

import FIS.iLUVit.domain.embeddable.Area;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;

public class CenterQueryMethod {

    protected BooleanExpression areasIn(List<Area> areas){
        return areas.size() == 0 ? null : center.area.in(areas);
    }

    protected BooleanExpression interestedAgeEq(Integer age) {
        return age == null ? null : center.maxAge.goe(age).and(center.minAge.loe(age));
    }

    protected BooleanExpression kindOfEq(String kindOf) {
        return kindOf == null || kindOf.equals("") || kindOf.equals("ALL") ? null : center.kindOf.eq(kindOf);
    }

}
