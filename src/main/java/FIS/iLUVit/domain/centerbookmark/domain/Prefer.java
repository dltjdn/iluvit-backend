package FIS.iLUVit.domain.centerbookmark.domain;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.parent.domain.Parent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"parent_id", "center_id"}
                )
        }
)
public class Prefer extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    public static Prefer createPrefer(Parent parent, Center center) {
        Prefer prefer = new Prefer();
        prefer.center = center;
        prefer.parent = parent;
        return prefer;
    }

    @Builder
    public Prefer(Long id, Parent parent, Center center) {
        this.id = id;
        this.parent = parent;
        this.center = center;
    }
}
