package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresentationFullAlarm is a Querydsl query type for PresentationFullAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPresentationFullAlarm extends EntityPathBase<PresentationFullAlarm> {

    private static final long serialVersionUID = 2112979077L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresentationFullAlarm presentationFullAlarm = new QPresentationFullAlarm("presentationFullAlarm");

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

    public QPresentationFullAlarm(String variable) {
        this(PresentationFullAlarm.class, forVariable(variable), INITS);
    }

    public QPresentationFullAlarm(Path<? extends PresentationFullAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresentationFullAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresentationFullAlarm(PathMetadata metadata, PathInits inits) {
        this(PresentationFullAlarm.class, metadata, inits);
    }

    public QPresentationFullAlarm(Class<? extends PresentationFullAlarm> type, PathMetadata metadata, PathInits inits) {
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

