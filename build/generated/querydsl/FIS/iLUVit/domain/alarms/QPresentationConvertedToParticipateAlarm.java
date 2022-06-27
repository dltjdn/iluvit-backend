package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentationConvertedToParticipateAlarm is a Querydsl query type for PresentationConvertedToParticipateAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentationConvertedToParticipateAlarm extends EntityPathBase<PresentationConvertedToParticipateAlarm> {

    private static final long serialVersionUID = 2067772323L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentationConvertedToParticipateAlarm presentationConvertedToParticipateAlarm = new QPresentationConvertedToParticipateAlarm("presentationConvertedToParticipateAlarm");

    public final QAlarm _super;

    public final FIS.iLUVit.domain.QCenter center;

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

    public QPresentationConvertedToParticipateAlarm(String variable) {
        this(PresentationConvertedToParticipateAlarm.class, forVariable(variable), INITS);
    }

    public QPresentationConvertedToParticipateAlarm(Path<? extends PresentationConvertedToParticipateAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentationConvertedToParticipateAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentationConvertedToParticipateAlarm(PathMetadata metadata, PathInits inits) {
        this(PresentationConvertedToParticipateAlarm.class, metadata, inits);
    }

    public QPresentationConvertedToParticipateAlarm(Class<? extends PresentationConvertedToParticipateAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new FIS.iLUVit.domain.QCenter(forProperty("center"), inits.get("center")) : null;
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.presentation = inits.isInitialized("presentation") ? new FIS.iLUVit.domain.QPresentation(forProperty("presentation"), inits.get("presentation")) : null;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

