package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParent is a Querydsl query type for Parent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParent extends EntityPathBase<Parent> {

    private static final long serialVersionUID = 698679251L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParent parent = new QParent("parent");

    public final QUser _super = new QUser(this);

    //inherited
    public final EnumPath<FIS.iLUVit.domain.enumtype.Auth> auth = _super.auth;

    public final ListPath<Child, QChild> children = this.<Child, QChild>createList("children", Child.class, QChild.class, PathInits.DIRECT2);

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

    public final NumberPath<Integer> interestAge = createNumber("interestAge", Integer.class);

    //inherited
    public final StringPath loginId = _super.loginId;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath nickName = _super.nickName;

    public final ListPath<Participation, QParticipation> participations = this.<Participation, QParticipation>createList("participations", Participation.class, QParticipation.class, PathInits.DIRECT2);

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final StringPath phoneNumber = _super.phoneNumber;

    //inherited
    public final ListPath<Chat, QChat> receiveChats = _super.receiveChats;

    //inherited
    public final ListPath<Chat, QChat> sendChats = _super.sendChats;

    public final FIS.iLUVit.domain.embeddable.QTheme theme;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QParent(String variable) {
        this(Parent.class, forVariable(variable), INITS);
    }

    public QParent(Path<? extends Parent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParent(PathMetadata metadata, PathInits inits) {
        this(Parent.class, metadata, inits);
    }

    public QParent(Class<? extends Parent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.theme = inits.isInitialized("theme") ? new FIS.iLUVit.domain.embeddable.QTheme(forProperty("theme")) : null;
    }

}

