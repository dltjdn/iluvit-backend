package FIS.iLUVit.dto.center;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CenterRecommendDto {
    private Long centerId;
    private String centerName;
    private String profileImage;

    public CenterRecommendDto(Center center){
        this.centerId = center.getId();
        this.centerName = center.getName();
        this.profileImage = center.getProfileImagePath();
    }

}
