package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QScrapPost is a Querydsl query type for ScrapPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScrapPost extends EntityPathBase<ScrapPost> {

    private static final long serialVersionUID = -1108324280L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScrapPost scrapPost = new QScrapPost("scrapPost");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public final QScrap scrap;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QScrapPost(String variable) {
        this(ScrapPost.class, forVariable(variable), INITS);
    }

    public QScrapPost(Path<? extends ScrapPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QScrapPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QScrapPost(PathMetadata metadata, PathInits inits) {
        this(ScrapPost.class, metadata, inits);
    }

    public QScrapPost(Class<? extends ScrapPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.scrap = inits.isInitialized("scrap") ? new QScrap(forProperty("scrap"), inits.get("scrap")) : null;
    }

}

