package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrefer is a Querydsl query type for Prefer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrefer extends EntityPathBase<Prefer> {

    private static final long serialVersionUID = 713992505L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrefer prefer = new QPrefer("prefer");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCenter center;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> dateTime = createDateTime("dateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QParent parent;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QPrefer(String variable) {
        this(Prefer.class, forVariable(variable), INITS);
    }

    public QPrefer(Path<? extends Prefer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrefer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrefer(PathMetadata metadata, PathInits inits) {
        this(Prefer.class, metadata, inits);
    }

    public QPrefer(Class<? extends Prefer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
        this.parent = inits.isInitialized("parent") ? new QParent(forProperty("parent"), inits.get("parent")) : null;
    }

}

