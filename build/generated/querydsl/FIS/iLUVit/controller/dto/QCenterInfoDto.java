package FIS.iLUVit.controller.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * FIS.iLUVit.controller.dto.QCenterInfoDto is a Querydsl Projection type for CenterInfoDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCenterInfoDto extends ConstructorExpression<CenterInfoDto> {

    private static final long serialVersionUID = -1896040452L;

    public QCenterInfoDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> centerName, com.querydsl.core.types.Expression<String> centerAddress) {
        super(CenterInfoDto.class, new Class<?>[]{long.class, String.class, String.class}, id, centerName, centerAddress);
    }

}

