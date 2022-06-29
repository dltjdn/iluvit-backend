package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentationPeriodClosedAlarm is a Querydsl query type for PresentationPeriodClosedAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentationPeriodClosedAlarm extends EntityPathBase<PresentationPeriodClosedAlarm> {

    private static final long serialVersionUID = -1871179705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentationPeriodClosedAlarm presentationPeriodClosedAlarm = new QPresentationPeriodClosedAlarm("presentationPeriodClosedAlarm");

    public final QAlarm _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final StringPath dtype;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath message;

    public final FIS.iLUVit.domain.QPresentation presentation;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QPresentationPeriodClosedAlarm(String variable) {
        this(PresentationPeriodClosedAlarm.class, forVariable(variable), INITS);
    }

    public QPresentationPeriodClosedAlarm(Path<? extends PresentationPeriodClosedAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentationPeriodClosedAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentationPeriodClosedAlarm(PathMetadata metadata, PathInits inits) {
        this(PresentationPeriodClosedAlarm.class, metadata, inits);
    }

    public QPresentationPeriodClosedAlarm(Class<? extends PresentationPeriodClosedAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.presentation = inits.isInitialized("presentation") ? new FIS.iLUVit.domain.QPresentation(forProperty("presentation"), inits.get("presentation")) : null;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

