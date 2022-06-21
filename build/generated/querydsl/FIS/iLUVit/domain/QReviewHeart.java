package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewHeart is a Querydsl query type for ReviewHeart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewHeart extends EntityPathBase<ReviewHeart> {

    private static final long serialVersionUID = -513607099L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewHeart reviewHeart = new QReviewHeart("reviewHeart");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReview review;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final QUser user;

    public QReviewHeart(String variable) {
        this(ReviewHeart.class, forVariable(variable), INITS);
    }

    public QReviewHeart(Path<? extends ReviewHeart> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewHeart(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewHeart(PathMetadata metadata, PathInits inits) {
        this(ReviewHeart.class, metadata, inits);
    }

    public QReviewHeart(Class<? extends ReviewHeart> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

