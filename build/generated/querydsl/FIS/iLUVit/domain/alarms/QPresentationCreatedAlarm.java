package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentationCreatedAlarm is a Querydsl query type for PresentationCreatedAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentationCreatedAlarm extends EntityPathBase<PresentationCreatedAlarm> {

    private static final long serialVersionUID = -1977454106L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentationCreatedAlarm presentationCreatedAlarm = new QPresentationCreatedAlarm("presentationCreatedAlarm");

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

    public QPresentationCreatedAlarm(String variable) {
        this(PresentationCreatedAlarm.class, forVariable(variable), INITS);
    }

    public QPresentationCreatedAlarm(Path<? extends PresentationCreatedAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentationCreatedAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentationCreatedAlarm(PathMetadata metadata, PathInits inits) {
        this(PresentationCreatedAlarm.class, metadata, inits);
    }

    public QPresentationCreatedAlarm(Class<? extends PresentationCreatedAlarm> type, PathMetadata metadata, PathInits inits) {
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

