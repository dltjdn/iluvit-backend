package FIS.iLUVit.repository.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * FIS.iLUVit.repository.dto.QCenterPreview is a Querydsl Projection type for CenterPreview
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCenterPreview extends ConstructorExpression<CenterPreview> {

    private static final long serialVersionUID = 1368949153L;

    public QCenterPreview(com.querydsl.core.types.Expression<? extends FIS.iLUVit.domain.Center> center, com.querydsl.core.types.Expression<Double> starAverage) {
        super(CenterPreview.class, new Class<?>[]{FIS.iLUVit.domain.Center.class, double.class}, center, starAverage);
    }

}

