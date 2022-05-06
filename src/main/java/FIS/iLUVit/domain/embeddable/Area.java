package FIS.iLUVit.domain.embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Area {
    private String sido;
    private String sigungu;
}
