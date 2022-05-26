package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCenter is a Querydsl query type for Center
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCenter extends EntityPathBase<Center> {

    private static final long serialVersionUID = 330089342L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCenter center = new QCenter("center");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<AddInfo, QAddInfo> addInfos = this.<AddInfo, QAddInfo>createList("addInfos", AddInfo.class, QAddInfo.class, PathInits.DIRECT2);

    public final StringPath address = createString("address");

    public final FIS.iLUVit.domain.embeddable.QArea area;

    public final FIS.iLUVit.domain.embeddable.QBasicInfra basicInfra;

    public final FIS.iLUVit.domain.embeddable.QClassInfo classInfo;

    public final FIS.iLUVit.domain.embeddable.QCostInfo costInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Integer> curChildCnt = createNumber("curChildCnt", Integer.class);

    public final StringPath director = createString("director");

    public final StringPath endTime = createString("endTime");

    public final StringPath estDate = createString("estDate");

    public final StringPath estType = createString("estType");

    public final StringPath homepage = createString("homepage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> imgCnt = createNumber("imgCnt", Integer.class);

    public final StringPath introText = createString("introText");

    public final EnumPath<FIS.iLUVit.domain.enumtype.KindOf> kindOf = createEnum("kindOf", FIS.iLUVit.domain.enumtype.KindOf.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final NumberPath<Integer> maxAge = createNumber("maxAge", Integer.class);

    public final NumberPath<Integer> maxChildCnt = createNumber("maxChildCnt", Integer.class);

    public final NumberPath<Integer> minAge = createNumber("minAge", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath offerService = createString("offerService");

    public final FIS.iLUVit.domain.embeddable.QOtherInfo otherInfo;

    public final StringPath owner = createString("owner");

    public final ListPath<Presentation, QPresentation> presentations = this.<Presentation, QPresentation>createList("presentations", Presentation.class, QPresentation.class, PathInits.DIRECT2);

    public final ListPath<Program, QProgram> programs = this.<Program, QProgram>createList("programs", Program.class, QProgram.class, PathInits.DIRECT2);

    public final BooleanPath recruit = createBoolean("recruit");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final BooleanPath signed = createBoolean("signed");

    public final StringPath startTime = createString("startTime");

    public final StringPath status = createString("status");

    public final FIS.iLUVit.domain.embeddable.QTeacherInfo teacherInfo;

    public final StringPath tel = createString("tel");

    public final FIS.iLUVit.domain.embeddable.QTheme theme;

    public final DatePath<java.time.LocalDate> updateDate = createDate("updateDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final NumberPath<Integer> videoCnt = createNumber("videoCnt", Integer.class);

    public final NumberPath<Integer> waitingNum = createNumber("waitingNum", Integer.class);

    public final StringPath zipcode = createString("zipcode");

    public QCenter(String variable) {
        this(Center.class, forVariable(variable), INITS);
    }

    public QCenter(Path<? extends Center> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCenter(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCenter(PathMetadata metadata, PathInits inits) {
        this(Center.class, metadata, inits);
    }

    public QCenter(Class<? extends Center> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.area = inits.isInitialized("area") ? new FIS.iLUVit.domain.embeddable.QArea(forProperty("area")) : null;
        this.basicInfra = inits.isInitialized("basicInfra") ? new FIS.iLUVit.domain.embeddable.QBasicInfra(forProperty("basicInfra")) : null;
        this.classInfo = inits.isInitialized("classInfo") ? new FIS.iLUVit.domain.embeddable.QClassInfo(forProperty("classInfo")) : null;
        this.costInfo = inits.isInitialized("costInfo") ? new FIS.iLUVit.domain.embeddable.QCostInfo(forProperty("costInfo")) : null;
        this.otherInfo = inits.isInitialized("otherInfo") ? new FIS.iLUVit.domain.embeddable.QOtherInfo(forProperty("otherInfo")) : null;
        this.teacherInfo = inits.isInitialized("teacherInfo") ? new FIS.iLUVit.domain.embeddable.QTeacherInfo(forProperty("teacherInfo")) : null;
        this.theme = inits.isInitialized("theme") ? new FIS.iLUVit.domain.embeddable.QTheme(forProperty("theme")) : null;
    }

}

