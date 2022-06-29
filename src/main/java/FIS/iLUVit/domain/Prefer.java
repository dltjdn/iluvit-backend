package FIS.iLUVit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Prefer extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    private LocalDateTime dateTime;// 찜한 시간

    public static Prefer createPrefer(Parent parent, Center center){
        Prefer prefer = new Prefer();
        prefer.center = center;
        prefer.parent = parent;
        return prefer;
    }
}
