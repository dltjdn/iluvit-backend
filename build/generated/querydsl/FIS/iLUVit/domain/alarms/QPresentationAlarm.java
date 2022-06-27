package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentationAlarm is a Querydsl query type for PresentationAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentationAlarm extends EntityPathBase<PresentationAlarm> {

    private static final long serialVersionUID = 2095090516L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentationAlarm presentationAlarm = new QPresentationAlarm("presentationAlarm");

    public final QAlarm _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final StringPath dtype;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath message;

    //inherited
    public final StringPath mode;

    public final FIS.iLUVit.domain.QPresentation presentation;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QPresentationAlarm(String variable) {
        this(PresentationAlarm.class, forVariable(variable), INITS);
    }

    public QPresentationAlarm(Path<? extends PresentationAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentationAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentationAlarm(PathMetadata metadata, PathInits inits) {
        this(PresentationAlarm.class, metadata, inits);
    }

    public QPresentationAlarm(Class<? extends PresentationAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.mode = _super.mode;
        this.presentation = inits.isInitialized("presentation") ? new FIS.iLUVit.domain.QPresentation(forProperty("presentation"), inits.get("presentation")) : null;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

