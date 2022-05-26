package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChild is a Querydsl query type for Child
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChild extends EntityPathBase<Child> {

    private static final long serialVersionUID = 426374355L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChild child = new QChild("child");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final EnumPath<FIS.iLUVit.domain.enumtype.Approval> approval = createEnum("approval", FIS.iLUVit.domain.enumtype.Approval.class);

    public final StringPath birthDate = createString("birthDate");

    public final QCenter center;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath hasProfileImg = createBoolean("hasProfileImg");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QParent parent;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QChild(String variable) {
        this(Child.class, forVariable(variable), INITS);
    }

    public QChild(Path<? extends Child> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChild(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChild(PathMetadata metadata, PathInits inits) {
        this(Child.class, metadata, inits);
    }

    public QChild(Class<? extends Child> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
        this.parent = inits.isInitialized("parent") ? new QParent(forProperty("parent"), inits.get("parent")) : null;
    }

}

