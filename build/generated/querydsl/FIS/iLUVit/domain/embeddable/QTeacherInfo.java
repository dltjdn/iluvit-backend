package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTeacherInfo is a Querydsl query type for TeacherInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QTeacherInfo extends BeanPath<TeacherInfo> {

    private static final long serialVersionUID = -381529936L;

    public static final QTeacherInfo teacherInfo = new QTeacherInfo("teacherInfo");

    public final NumberPath<Integer> dur12 = createNumber("dur12", Integer.class);

    public final NumberPath<Integer> dur24 = createNumber("dur24", Integer.class);

    public final NumberPath<Integer> dur46 = createNumber("dur46", Integer.class);

    public final NumberPath<Integer> dur6_ = createNumber("dur6_", Integer.class);

    public final NumberPath<Integer> dur_1 = createNumber("dur_1", Integer.class);

    public final NumberPath<Integer> totalCnt = createNumber("totalCnt", Integer.class);

    public QTeacherInfo(String variable) {
        super(TeacherInfo.class, forVariable(variable));
    }

    public QTeacherInfo(Path<? extends TeacherInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeacherInfo(PathMetadata metadata) {
        super(TeacherInfo.class, metadata);
    }

}

