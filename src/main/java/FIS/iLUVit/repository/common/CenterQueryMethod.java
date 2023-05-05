package FIS.iLUVit.repository.common;

import FIS.iLUVit.domain.common.embeddable.Area;
import FIS.iLUVit.domain.common.embeddable.Theme;
import FIS.iLUVit.domain.iluvit.enumtype.KindOf;
import FIS.iLUVit.exception.CenterException;
import com.querydsl.core.types.dsl.*;

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

    protected BooleanExpression kindOfEq(KindOf kindOf) {
        return kindOf == null || kindOf == KindOf.ALL ? null : center.kindOf.eq(kindOf);
    }

    protected BooleanExpression themeEq(Theme theme) {
        try {
            if (theme == null) return null;
            // 관심 목록 추출한 trueList
            List<String> trueList = theme.trueList();
            // BooleanExpression 초기화 => in 절도 사용 할 수 없다. theme 별로 나뉘어져 있으므로
            BooleanExpression booleanExpression = null;
            for (String name : trueList) {
                // trueList 에 해당하는 BooleanExp 가져온다.
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
            return booleanExpression;
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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

    protected BooleanExpression areaEq(String sido, String sigungu) {
        if ((sido == null || sido.isEmpty()) && (sigungu == null || sigungu.isEmpty())) {
            return null;
        } else if (sigungu == null || sigungu.isEmpty()) {
            return center.area.sido.eq(sido);
        } else if (sido == null || sido.isEmpty()) {
            return center.area.sigungu.eq(sigungu);
        } else {
            return center.area.sido.eq(sido).and(center.area.sigungu.eq(sigungu));
        }
    }

    protected BooleanExpression centerNameEq(String centerName) {
        return centerName == null ? null : center.name.contains(centerName);
    }

    protected BooleanExpression userIdEq(NumberPath<Long> pathBase, Long userId) {
        return userId == null ? null : pathBase.eq(userId);
    }

}
