package FIS.iLUVit.domain.center.repository;

import FIS.iLUVit.domain.center.domain.Area;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.center.domain.KindOf;
import FIS.iLUVit.domain.center.exception.CenterException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


import static FIS.iLUVit.domain.center.domain.QCenter.center;
import static com.querydsl.core.types.dsl.MathExpressions.*;

public class CenterQueryMethod {
    /**
        areas 리스트가 비어있는지 확인하여 비어있는 경우 null을 반환하고, 비어있지 않은 경우 center.area.in(areas)를 반환합니다.
     */
    protected BooleanExpression areasIn(List<Area> areas) {
        return areas.size() == 0 ? null : center.area.in(areas);
    }

    /**
        age 값이 null인지 확인하여 null을 반환하고, age 값이 실제 값인 경우 center.maxAge.goe(age).and(center.minAge.loe(age))를 반환합니다.
     */
    protected BooleanExpression interestedAgeEq(Integer age) {
        return age == null ? null : center.maxAge.goe(age).and(center.minAge.loe(age));
    }

    /**
        kindOf 값이 null이거나 KindOf.ALL과 동일한지 확인하여 null을 반환하고, kindOf 값이 실제 값인 경우 center.kindOf.eq(kindOf)를 반환합니다.
     */
    protected BooleanExpression kindOfEq(KindOf kindOf) {
        return kindOf == null || kindOf == KindOf.ALL ? null : center.kindOf.eq(kindOf);
    }

    /**
     * theme 객체에 포함된 true 값이 있는 필드들을 사용하여 BooleanExpression을 생성하고, 이를 통해 쿼리에서 필터링
     */
    protected BooleanExpression themeEq(Theme theme) {
        try {
            if (theme == null) return null;
            // 관심 목록 추출한 trueList
            List<String> trueList = theme.trueList();

            // BooleanExpression 초기화 => in 절도 사용 할 수 없다. theme 별로 나뉘어져 있으므다로
            BooleanExpression booleanExpression = null;
            for (String name : trueList) { // trueList 에 해당하는 BooleanExp 가져온다.

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

    /**
        위도와 경도를 사용하여 두 지점 사이의 거리를 계산하는 수식을 작성합니다. (Harversine 공식을 적용하여 지구상에서의 거리를 계산하고, 계산된 거리를 표현하는 NumberExpression 객체를 반환함)
     */
    protected NumberExpression distanceRange(double longitude, double latitude) {
        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);
        return distanceEx;
    }

    /**
        시도와 시군구 값을 기반으로 지역에 대한 조건을 생성합니다. (시도와 시군구 값이 모두 비어있거나 null인 경우 null을 반환하며, 시군구 값이 비어있는 경우 시도에 대한 조건을 생성하여 반환함)
     */
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

    /**
        센터 이름을 기반으로 조건을 생성합니다. (센터 이름이 null인 경우 null을 반환하며, 그렇지 않은 경우 센터의 이름에 주어진 센터 이름을 포함하는지를 검사하는 조건을 생성하여 반환함)
     */
    protected BooleanExpression centerNameEq(String centerName) {
        return centerName == null ? null : center.name.contains(centerName);
    }


}
