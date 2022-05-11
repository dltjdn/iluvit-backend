package FIS.iLUVit.repository;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import com.querydsl.core.types.dsl.*;

import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;
import static com.querydsl.core.types.dsl.MathExpressions.*;

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

    protected BooleanExpression themeEq(Theme theme){
        return theme == null ? null : center.theme.eq(theme);
    }

    protected NumberExpression distanceRange(double longitude, double latitude){
        NumberExpression<Double> distanceExpression =
                acos(sin(radians(center.latitude)))
                .multiply(sin(radians(Expressions.constant(latitude)))
                .add(cos(radians(center.latitude))
                        .multiply(cos(radians(Expressions.constant(latitude)))
                        .multiply(cos(radians(center.longitude)).subtract(radians(Expressions.constant(longitude))))))).multiply(6371);
        return distanceExpression;
    }

}
