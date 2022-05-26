package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtherInfo is a Querydsl query type for OtherInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QOtherInfo extends BeanPath<OtherInfo> {

    private static final long serialVersionUID = -804193058L;

    public static final QOtherInfo otherInfo = new QOtherInfo("otherInfo");

    public final StringPath participation = createString("participation");

    public final StringPath visited = createString("visited");

    public QOtherInfo(String variable) {
        super(OtherInfo.class, forVariable(variable));
    }

    public QOtherInfo(Path<? extends OtherInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtherInfo(PathMetadata metadata) {
        super(OtherInfo.class, metadata);
    }

}

