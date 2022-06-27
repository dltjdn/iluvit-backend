package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCenterApprovalReceivedAlarm is a Querydsl query type for CenterApprovalReceivedAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCenterApprovalReceivedAlarm extends EntityPathBase<CenterApprovalReceivedAlarm> {

    private static final long serialVersionUID = 596145621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCenterApprovalReceivedAlarm centerApprovalReceivedAlarm = new QCenterApprovalReceivedAlarm("centerApprovalReceivedAlarm");

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
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QCenterApprovalReceivedAlarm(String variable) {
        this(CenterApprovalReceivedAlarm.class, forVariable(variable), INITS);
    }

    public QCenterApprovalReceivedAlarm(Path<? extends CenterApprovalReceivedAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCenterApprovalReceivedAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCenterApprovalReceivedAlarm(PathMetadata metadata, PathInits inits) {
        this(CenterApprovalReceivedAlarm.class, metadata, inits);
    }

    public QCenterApprovalReceivedAlarm(Class<? extends CenterApprovalReceivedAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

