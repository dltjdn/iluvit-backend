package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCostInfo is a Querydsl query type for CostInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCostInfo extends BeanPath<CostInfo> {

    private static final long serialVersionUID = 858754331L;

    public static final QCostInfo costInfo = new QCostInfo("costInfo");

    public final NumberPath<Integer> cost0 = createNumber("cost0", Integer.class);

    public final NumberPath<Integer> cost1 = createNumber("cost1", Integer.class);

    public final NumberPath<Integer> cost2 = createNumber("cost2", Integer.class);

    public final NumberPath<Integer> cost3 = createNumber("cost3", Integer.class);

    public final NumberPath<Integer> cost4 = createNumber("cost4", Integer.class);

    public final NumberPath<Integer> cost5 = createNumber("cost5", Integer.class);

    public QCostInfo(String variable) {
        super(CostInfo.class, forVariable(variable));
    }

    public QCostInfo(Path<? extends CostInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCostInfo(PathMetadata metadata) {
        super(CostInfo.class, metadata);
    }

}

