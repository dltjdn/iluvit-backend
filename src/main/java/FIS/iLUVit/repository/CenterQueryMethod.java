package FIS.iLUVit.repository;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.exception.CenterException;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;
import static com.querydsl.core.types.dsl.MathExpressions.*;

public class CenterQueryMethod {

    protected BooleanExpression areasIn(List<Area> areas) {
        return areas.size() == 0 ? null : center.area.in(areas);
    }

    protected BooleanExpression interestedAgeEq(Integer age) {
        return age == null ? null : center.maxAge.goe(age).and(center.minAge.loe(age));
    }

    protected BooleanExpression kindOfEq(String kindOf) {
        return kindOf == null || kindOf.equals("") || kindOf.equals("ALL") ? null : center.kindOf.eq(kindOf);
    }

    protected BooleanExpression themeEq(Theme theme) {
        try {
            List<String> trueList = theme.trueList();
            BooleanExpression booleanExpression = null;
            for (String name : trueList) {
                BooleanPath type = (BooleanPath) center.theme.getClass().getDeclaredField(name).get(center.theme);
                Method eq = center.theme.getClass().getDeclaredField(name).getType().getMethod("eq", Boolean.class);
                if (booleanExpression == null) {
                    eq.setAccessible(true);
                    booleanExpression = (BooleanExpression) eq.invoke(type, true);
                } else {
                    eq.setAccessible(true);
                    booleanExpression = booleanExpression.or((BooleanExpression) eq.invoke(type, true));
                }
            }
            return theme == null ? null : booleanExpression;
        } catch(NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            throw new CenterException("센터 repository에서 오류");
        }
    }

    protected NumberExpression distanceRange(double longitude, double latitude) {
        NumberExpression<Double> distanceExpression =
                acos(sin(radians(center.latitude)))
                        .multiply(sin(radians(Expressions.constant(latitude)))
                                .add(cos(radians(center.latitude))
                                        .multiply(cos(radians(Expressions.constant(latitude)))
                                                .multiply(cos(radians(center.longitude)).subtract(radians(Expressions.constant(longitude))))))).multiply(6371);
        return distanceExpression;
    }

}
