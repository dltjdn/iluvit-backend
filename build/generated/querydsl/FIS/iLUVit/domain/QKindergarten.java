package FIS.iLUVit.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QKindergarten is a Querydsl query type for Kindergarten
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKindergarten extends EntityPathBase<Kindergarten> {

    private static final long serialVersionUID = 222844207L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QKindergarten kindergarten = new QKindergarten("kindergarten");

    public final QCenter _super;

    //inherited
    public final StringPath addInfo;

    //inherited
    public final StringPath address;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QArea area;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QBasicInfra basicInfra;

    //inherited
    public final ListPath<Board, QBoard> boards;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QClassInfo classInfo;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QCostInfo costInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    //inherited
    public final NumberPath<Integer> curChildCnt;

    //inherited
    public final StringPath director;

    //inherited
    public final StringPath endTime;

    //inherited
    public final StringPath estDate;

    //inherited
    public final StringPath estType;

    //inherited
    public final StringPath homepage;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final NumberPath<Integer> imgCnt;

    //inherited
    public final StringPath introText;

    //inherited
    public final EnumPath<FIS.iLUVit.domain.enumtype.KindOf> kindOf;

    //inherited
    public final NumberPath<Double> latitude;

    //inherited
    public final NumberPath<Double> longitude;

    //inherited
    public final NumberPath<Integer> maxAge;

    //inherited
    public final NumberPath<Integer> maxChildCnt;

    //inherited
    public final NumberPath<Integer> minAge;

    //inherited
    public final StringPath name;

    //inherited
    public final StringPath offerService;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QOtherInfo otherInfo;

    //inherited
    public final StringPath owner;

    //inherited
    public final ListPath<Presentation, QPresentation> presentations;

    //inherited
    public final StringPath program;

    //inherited
    public final BooleanPath recruit;

    //inherited
    public final ListPath<Review, QReview> reviews;

    //inherited
    public final NumberPath<Integer> score;

    //inherited
    public final BooleanPath signed;

    //inherited
    public final StringPath startTime;

    //inherited
    public final StringPath status;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QTeacherInfo teacherInfo;

    //inherited
    public final ListPath<Teacher, QTeacher> teachers;

    //inherited
    public final StringPath tel;

    // inherited
    public final FIS.iLUVit.domain.embeddable.QTheme theme;

    //inherited
    public final DatePath<java.time.LocalDate> updateDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate;

    //inherited
    public final NumberPath<Integer> videoCnt;

    //inherited
    public final NumberPath<Integer> waitingNum;

    //inherited
    public final StringPath zipcode;

    public QKindergarten(String variable) {
        this(Kindergarten.class, forVariable(variable), INITS);
    }

    public QKindergarten(Path<? extends Kindergarten> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QKindergarten(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QKindergarten(PathMetadata metadata, PathInits inits) {
        this(Kindergarten.class, metadata, inits);
    }

    public QKindergarten(Class<? extends Kindergarten> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QCenter(type, metadata, inits);
        this.addInfo = _super.addInfo;
        this.address = _super.address;
        this.area = _super.area;
        this.basicInfra = _super.basicInfra;
        this.boards = _super.boards;
        this.classInfo = _super.classInfo;
        this.costInfo = _super.costInfo;
        this.createdDate = _super.createdDate;
        this.curChildCnt = _super.curChildCnt;
        this.director = _super.director;
        this.endTime = _super.endTime;
        this.estDate = _super.estDate;
        this.estType = _super.estType;
        this.homepage = _super.homepage;
        this.id = _super.id;
        this.imgCnt = _super.imgCnt;
        this.introText = _super.introText;
        this.kindOf = _super.kindOf;
        this.latitude = _super.latitude;
        this.longitude = _super.longitude;
        this.maxAge = _super.maxAge;
        this.maxChildCnt = _super.maxChildCnt;
        this.minAge = _super.minAge;
        this.name = _super.name;
        this.offerService = _super.offerService;
        this.otherInfo = _super.otherInfo;
        this.owner = _super.owner;
        this.presentations = _super.presentations;
        this.program = _super.program;
        this.recruit = _super.recruit;
        this.reviews = _super.reviews;
        this.score = _super.score;
        this.signed = _super.signed;
        this.startTime = _super.startTime;
        this.status = _super.status;
        this.teacherInfo = _super.teacherInfo;
        this.teachers = _super.teachers;
        this.tel = _super.tel;
        this.theme = _super.theme;
        this.updateDate = _super.updateDate;
        this.updatedDate = _super.updatedDate;
        this.videoCnt = _super.videoCnt;
        this.waitingNum = _super.waitingNum;
        this.zipcode = _super.zipcode;
    }

}

