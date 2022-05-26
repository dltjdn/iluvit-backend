package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasicInfra is a Querydsl query type for BasicInfra
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QBasicInfra extends BeanPath<BasicInfra> {

    private static final long serialVersionUID = -101921470L;

    public static final QBasicInfra basicInfra = new QBasicInfra("basicInfra");

    public final NumberPath<Integer> buildingYear = createNumber("buildingYear", Integer.class);

    public final NumberPath<Integer> BusCnt = createNumber("BusCnt", Integer.class);

    public final NumberPath<Integer> cctvCnt = createNumber("cctvCnt", Integer.class);

    public final BooleanPath hasBackpack = createBoolean("hasBackpack");

    public final BooleanPath hasBus = createBoolean("hasBus");

    public final BooleanPath hasCCTV = createBoolean("hasCCTV");

    public final BooleanPath hasHandWriteNote = createBoolean("hasHandWriteNote");

    public final BooleanPath hasKidsNote = createBoolean("hasKidsNote");

    public final BooleanPath hasPhysics = createBoolean("hasPhysics");

    public final BooleanPath hasPlayground = createBoolean("hasPlayground");

    public final BooleanPath hasSwimPool = createBoolean("hasSwimPool");

    public final BooleanPath hasUniform = createBoolean("hasUniform");

    public QBasicInfra(String variable) {
        super(BasicInfra.class, forVariable(variable));
    }

    public QBasicInfra(Path<? extends BasicInfra> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBasicInfra(PathMetadata metadata) {
        super(BasicInfra.class, metadata);
    }

}

