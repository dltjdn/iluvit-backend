package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 759754369L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath anonymous = createBoolean("anonymous");

    public final StringPath answer = createString("answer");

    public final DatePath<java.time.LocalDate> answerCreateDate = createDate("answerCreateDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> answerCreateTime = createTime("answerCreateTime", java.time.LocalTime.class);

    public final QCenter center;

    public final StringPath content = createString("content");

    public final DatePath<java.time.LocalDate> createDate = createDate("createDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final TimePath<java.time.LocalTime> createTime = createTime("createTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QParent parent;

    public final ListPath<ReviewHeart, QReviewHeart> reviewHearts = this.<ReviewHeart, QReviewHeart>createList("reviewHearts", ReviewHeart.class, QReviewHeart.class, PathInits.DIRECT2);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final QTeacher teacher;

    public final DatePath<java.time.LocalDate> updateDate = createDate("updateDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final TimePath<java.time.LocalTime> updateTime = createTime("updateTime", java.time.LocalTime.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
        this.parent = inits.isInitialized("parent") ? new QParent(forProperty("parent"), inits.get("parent")) : null;
        this.teacher = inits.isInitialized("teacher") ? new QTeacher(forProperty("teacher"), inits.get("teacher")) : null;
    }

}

