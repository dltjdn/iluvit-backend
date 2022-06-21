package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentation is a Querydsl query type for Presentation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentation extends EntityPathBase<Presentation> {

    private static final long serialVersionUID = -875613309L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentation presentation = new QPresentation("presentation");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCenter center;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> imgCnt = createNumber("imgCnt", Integer.class);

    public final StringPath place = createString("place");

    public final ListPath<PtDate, QPtDate> ptDates = this.<PtDate, QPtDate>createList("ptDates", PtDate.class, QPtDate.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final NumberPath<Integer> videoCnt = createNumber("videoCnt", Integer.class);

    public QPresentation(String variable) {
        this(Presentation.class, forVariable(variable), INITS);
    }

    public QPresentation(Path<? extends Presentation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentation(PathMetadata metadata, PathInits inits) {
        this(Presentation.class, metadata, inits);
    }

    public QPresentation(Class<? extends Presentation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
    }

}

