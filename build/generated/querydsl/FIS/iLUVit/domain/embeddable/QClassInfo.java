package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClassInfo is a Querydsl query type for ClassInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QClassInfo extends BeanPath<ClassInfo> {

    private static final long serialVersionUID = 1131983686L;

    public static final QClassInfo classInfo = new QClassInfo("classInfo");

    public final NumberPath<Integer> child_0 = createNumber("child_0", Integer.class);

    public final NumberPath<Integer> child_1 = createNumber("child_1", Integer.class);

    public final NumberPath<Integer> child_2 = createNumber("child_2", Integer.class);

    public final NumberPath<Integer> child_3 = createNumber("child_3", Integer.class);

    public final NumberPath<Integer> child_4 = createNumber("child_4", Integer.class);

    public final NumberPath<Integer> child_5 = createNumber("child_5", Integer.class);

    public final NumberPath<Integer> child_spe = createNumber("child_spe", Integer.class);

    public final NumberPath<Integer> class_0 = createNumber("class_0", Integer.class);

    public final NumberPath<Integer> class_1 = createNumber("class_1", Integer.class);

    public final NumberPath<Integer> class_2 = createNumber("class_2", Integer.class);

    public final NumberPath<Integer> class_3 = createNumber("class_3", Integer.class);

    public final NumberPath<Integer> class_4 = createNumber("class_4", Integer.class);

    public final NumberPath<Integer> class_5 = createNumber("class_5", Integer.class);

    public QClassInfo(String variable) {
        super(ClassInfo.class, forVariable(variable));
    }

    public QClassInfo(Path<? extends ClassInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClassInfo(PathMetadata metadata) {
        super(ClassInfo.class, metadata);
    }

}

