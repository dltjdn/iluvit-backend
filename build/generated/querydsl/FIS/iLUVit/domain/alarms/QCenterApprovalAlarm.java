package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCenterApprovalAlarm is a Querydsl query type for CenterApprovalAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCenterApprovalAlarm extends EntityPathBase<CenterApprovalAlarm> {

    private static final long serialVersionUID = -1522991050L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCenterApprovalAlarm centerApprovalAlarm = new QCenterApprovalAlarm("centerApprovalAlarm");

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

    //inherited
    public final StringPath mode;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QCenterApprovalAlarm(String variable) {
        this(CenterApprovalAlarm.class, forVariable(variable), INITS);
    }

    public QCenterApprovalAlarm(Path<? extends CenterApprovalAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCenterApprovalAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCenterApprovalAlarm(PathMetadata metadata, PathInits inits) {
        this(CenterApprovalAlarm.class, metadata, inits);
    }

    public QCenterApprovalAlarm(Class<? extends CenterApprovalAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new FIS.iLUVit.domain.QCenter(forProperty("center"), inits.get("center")) : null;
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.mode = _super.mode;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

