package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthNumber is a Querydsl query type for AuthNumber
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthNumber extends EntityPathBase<AuthNumber> {

    private static final long serialVersionUID = 1238434234L;

    public static final QAuthNumber authNumber = new QAuthNumber("authNumber");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final EnumPath<FIS.iLUVit.domain.enumtype.AuthKind> authKind = createEnum("authKind", FIS.iLUVit.domain.enumtype.AuthKind.class);

    public final StringPath authNum = createString("authNum");

    public final DateTimePath<java.time.LocalDateTime> authTime = createDateTime("authTime", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath phoneNum = createString("phoneNum");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QAuthNumber(String variable) {
        super(AuthNumber.class, forVariable(variable));
    }

    public QAuthNumber(Path<? extends AuthNumber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthNumber(PathMetadata metadata) {
        super(AuthNumber.class, metadata);
    }

}

