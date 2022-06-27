package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAddInfo is a Querydsl query type for AddInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAddInfo extends EntityPathBase<AddInfo> {

    private static final long serialVersionUID = -171309338L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAddInfo addInfo = new QAddInfo("addInfo");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCenter center;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath info = createString("info");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QAddInfo(String variable) {
        this(AddInfo.class, forVariable(variable), INITS);
    }

    public QAddInfo(Path<? extends AddInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAddInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAddInfo(PathMetadata metadata, PathInits inits) {
        this(AddInfo.class, metadata, inits);
    }

    public QAddInfo(Class<? extends AddInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
    }

}

