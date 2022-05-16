package FIS.iLUVit.repository.dto;

import FIS.iLUVit.domain.embeddable.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenterBannerDto {
    @Id
    @GeneratedValue
    private Long id;
    private String name;                    // 시설명
    private Integer maxChildCnt;            // 정원
    private Integer curChildCnt;            // 현원
    private Boolean signed;                 // 원장의 가입 유무
    private Boolean recruit;                // 원아 모집중
    private Integer waitingNum;             // 원아 모집이 false 일때 대기자 수
}
