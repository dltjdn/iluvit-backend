package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPtDate is a Querydsl query type for PtDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPtDate extends EntityPathBase<PtDate> {

    private static final long serialVersionUID = 714852091L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPtDate ptDate = new QPtDate("ptDate");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> ablePersonNum = createNumber("ablePersonNum", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> participantCnt = createNumber("participantCnt", Integer.class);

    public final ListPath<Participation, QParticipation> participations = this.<Participation, QParticipation>createList("participations", Participation.class, QParticipation.class, PathInits.DIRECT2);

    public final QPresentation presentation;

    public final StringPath time = createString("time");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final NumberPath<Integer> waitingCnt = createNumber("waitingCnt", Integer.class);

    public final ListPath<Waiting, QWaiting> waitings = this.<Waiting, QWaiting>createList("waitings", Waiting.class, QWaiting.class, PathInits.DIRECT2);

    public QPtDate(String variable) {
        this(PtDate.class, forVariable(variable), INITS);
    }

    public QPtDate(Path<? extends PtDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPtDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPtDate(PathMetadata metadata, PathInits inits) {
        this(PtDate.class, metadata, inits);
    }

    public QPtDate(Class<? extends PtDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.presentation = inits.isInitialized("presentation") ? new QPresentation(forProperty("presentation"), inits.get("presentation")) : null;
    }

}

