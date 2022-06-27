package FIS.iLUVit.domain.alarms;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatAlarm is a Querydsl query type for ChatAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatAlarm extends EntityPathBase<ChatAlarm> {

    private static final long serialVersionUID = 103824086L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatAlarm chatAlarm = new QChatAlarm("chatAlarm");

    public final QAlarm _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final StringPath dtype;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath message;

    public final FIS.iLUVit.domain.QUser sender;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    // inherited
    public final FIS.iLUVit.domain.QUser user;

    public QChatAlarm(String variable) {
        this(ChatAlarm.class, forVariable(variable), INITS);
    }

    public QChatAlarm(Path<? extends ChatAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatAlarm(PathMetadata metadata, PathInits inits) {
        this(ChatAlarm.class, metadata, inits);
    }

    public QChatAlarm(Class<? extends ChatAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAlarm(type, metadata, inits);
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.id = _super.id;
        this.message = _super.message;
        this.sender = inits.isInitialized("sender") ? new FIS.iLUVit.domain.QUser(forProperty("sender")) : null;
        this.updatedDate = _super.updatedDate;
        this.user = _super.user;
    }

}

