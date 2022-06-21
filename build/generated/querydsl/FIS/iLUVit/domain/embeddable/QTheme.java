package FIS.iLUVit.domain.embeddable;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTheme is a Querydsl query type for Theme
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QTheme extends BeanPath<Theme> {

    private static final long serialVersionUID = 1216586441L;

    public static final QTheme theme = new QTheme("theme");

    public final BooleanPath animal = createBoolean("animal");

    public final BooleanPath art = createBoolean("art");

    public final BooleanPath buddhism = createBoolean("buddhism");

    public final BooleanPath camping = createBoolean("camping");

    public final BooleanPath catholic = createBoolean("catholic");

    public final BooleanPath christianity = createBoolean("christianity");

    public final BooleanPath clean = createBoolean("clean");

    public final BooleanPath coding = createBoolean("coding");

    public final BooleanPath english = createBoolean("english");

    public final BooleanPath foreigner = createBoolean("foreigner");

    public final BooleanPath genius = createBoolean("genius");

    public final BooleanPath manner = createBoolean("manner");

    public final BooleanPath math = createBoolean("math");

    public final BooleanPath music = createBoolean("music");

    public final BooleanPath nature = createBoolean("nature");

    public final BooleanPath plant = createBoolean("plant");

    public final BooleanPath sport = createBoolean("sport");

    public QTheme(String variable) {
        super(Theme.class, forVariable(variable));
    }

    public QTheme(Path<? extends Theme> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTheme(PathMetadata metadata) {
        super(Theme.class, metadata);
    }

}

