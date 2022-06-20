package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeacher is a Querydsl query type for Teacher
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeacher extends EntityPathBase<Teacher> {

    private static final long serialVersionUID = -461981223L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeacher teacher = new QTeacher("teacher");

    public final QUser _super = new QUser(this);

    //inherited
    public final StringPath address = _super.address;

    //inherited
    public final StringPath addressDetail = _super.addressDetail;

    public final EnumPath<FIS.iLUVit.domain.enumtype.Approval> approval = createEnum("approval", FIS.iLUVit.domain.enumtype.Approval.class);

    //inherited
    public final EnumPath<FIS.iLUVit.domain.enumtype.Auth> auth = _super.auth;

    public final QCenter center;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final StringPath dtype = _super.dtype;

    //inherited
    public final StringPath emailAddress = _super.emailAddress;

    //inherited
    public final BooleanPath hasProfileImg = _super.hasProfileImg;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath loginId = _super.loginId;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath nickName = _super.nickName;

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final StringPath phoneNumber = _super.phoneNumber;

    //inherited
    public final ListPath<Chat, QChat> receiveChats = _super.receiveChats;

    //inherited
    public final ListPath<Chat, QChat> sendChats = _super.sendChats;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QTeacher(String variable) {
        this(Teacher.class, forVariable(variable), INITS);
    }

    public QTeacher(Path<? extends Teacher> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeacher(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeacher(PathMetadata metadata, PathInits inits) {
        this(Teacher.class, metadata, inits);
    }

    public QTeacher(Class<? extends Teacher> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.center = inits.isInitialized("center") ? new QCenter(forProperty("center"), inits.get("center")) : null;
    }

}

