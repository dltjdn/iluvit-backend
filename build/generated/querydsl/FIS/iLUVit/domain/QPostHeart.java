package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostHeart is a Querydsl query type for PostHeart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostHeart extends EntityPathBase<PostHeart> {

    private static final long serialVersionUID = 853088189L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostHeart postHeart = new QPostHeart("postHeart");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final QUser user;

    public QPostHeart(String variable) {
        this(PostHeart.class, forVariable(variable), INITS);
    }

    public QPostHeart(Path<? extends PostHeart> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostHeart(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostHeart(PathMetadata metadata, PathInits inits) {
        this(PostHeart.class, metadata, inits);
    }

    public QPostHeart(Class<? extends PostHeart> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

