package FIS.iLUVit.repository.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * FIS.iLUVit.repository.dto.QCenterAndDistancePreview is a Querydsl Projection type for CenterAndDistancePreview
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCenterAndDistancePreview extends ConstructorExpression<CenterAndDistancePreview> {

    private static final long serialVersionUID = 1571841987L;

    public QCenterAndDistancePreview(com.querydsl.core.types.Expression<? extends FIS.iLUVit.domain.Center> center, com.querydsl.core.types.Expression<Double> starAverage) {
        super(CenterAndDistancePreview.class, new Class<?>[]{FIS.iLUVit.domain.Center.class, double.class}, center, starAverage);
    }

}

