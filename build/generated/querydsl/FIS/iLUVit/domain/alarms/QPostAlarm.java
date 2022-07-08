package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostAlarm is a Querydsl query type for PostAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostAlarm extends EntityPathBase<PostAlarm> {

    private static final long serialVersionUID = 486687374L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostAlarm postAlarm = new QPostAlarm("postAlarm");

    public final QAlarm _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final StringPath dtype;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath message;

    public final FIS.iLUVit.domain.QPost post;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QPostAlarm(String variable) {
        this(PostAlarm.class, forVariable(variable), INITS);
    }

    public QPostAlarm(Path<? extends PostAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostAlarm(PathMetadata metadata, PathInits inits) {
        this(PostAlarm.class, metadata, inits);
    }

    public QPostAlarm(Class<? extends PostAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.post = inits.isInitialized("post") ? new FIS.iLUVit.domain.QPost(forProperty("post"), inits.get("post")) : null;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

