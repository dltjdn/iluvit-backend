package FIS.iLUVit.dto.data;

import FIS.iLUVit.domain.embeddable.BasicInfra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KindergartenBasicInfraResponse {
    private String centerName;              // 시설명
    private BasicInfra basicInfra;          // 기본시설



}
