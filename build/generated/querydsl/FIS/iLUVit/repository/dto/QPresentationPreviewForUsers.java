package FIS.iLUVit.repository.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * FIS.iLUVit.repository.dto.QPresentationPreviewForUsers is a Querydsl Projection type for PresentationPreviewForUsers
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPresentationPreviewForUsers extends ConstructorExpression<PresentationPreviewForUsers> {

    private static final long serialVersionUID = 1329724315L;

    public QPresentationPreviewForUsers(com.querydsl.core.types.Expression<? extends FIS.iLUVit.domain.Presentation> presentation, com.querydsl.core.types.Expression<? extends FIS.iLUVit.domain.Center> center) {
        super(PresentationPreviewForUsers.class, new Class<?>[]{FIS.iLUVit.domain.Presentation.class, FIS.iLUVit.domain.Center.class}, presentation, center);
    }

}

