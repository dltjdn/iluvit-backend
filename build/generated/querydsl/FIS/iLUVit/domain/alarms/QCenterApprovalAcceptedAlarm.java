package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCenterApprovalAcceptedAlarm is a Querydsl query type for CenterApprovalAcceptedAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCenterApprovalAcceptedAlarm extends EntityPathBase<CenterApprovalAcceptedAlarm> {

    private static final long serialVersionUID = 2072931279L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCenterApprovalAcceptedAlarm centerApprovalAcceptedAlarm = new QCenterApprovalAcceptedAlarm("centerApprovalAcceptedAlarm");

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
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QCenterApprovalAcceptedAlarm(String variable) {
        this(CenterApprovalAcceptedAlarm.class, forVariable(variable), INITS);
    }

    public QCenterApprovalAcceptedAlarm(Path<? extends CenterApprovalAcceptedAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCenterApprovalAcceptedAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCenterApprovalAcceptedAlarm(PathMetadata metadata, PathInits inits) {
        this(CenterApprovalAcceptedAlarm.class, metadata, inits);
    }

    public QCenterApprovalAcceptedAlarm(Class<? extends CenterApprovalAcceptedAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new FIS.iLUVit.domain.QCenter(forProperty("center"), inits.get("center")) : null;
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

