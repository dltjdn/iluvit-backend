package FIS.iLUVit.controller.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * FIS.iLUVit.controller.dto.QCenterInfoForSignupDto is a Querydsl Projection type for CenterInfoForSignupDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCenterInfoForSignupDto extends ConstructorExpression<CenterInfoForSignupDto> {

    private static final long serialVersionUID = 1090234721L;

    public QCenterInfoForSignupDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> centerName, com.querydsl.core.types.Expression<String> centerAddress) {
        super(CenterInfoForSignupDto.class, new Class<?>[]{long.class, String.class, String.class}, id, centerName, centerAddress);
    }

}

