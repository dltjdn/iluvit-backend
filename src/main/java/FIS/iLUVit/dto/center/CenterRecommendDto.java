package FIS.iLUVit.dto.center;

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

    @QueryProjection
    public CenterRecommendDto(Long centerId, String centerName, String profileImage) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.profileImage = profileImage;
    }

    @Getter
    @NoArgsConstructor
    public static class CenterSearchFilterDto {

        @Size(min = 1, max = 3, message = "최소 1개 이상의 지역을 선택해야합니다")
        private List<Area> areas = new ArrayList<>();
        private Theme theme;
        private Integer interestedAge;
        private KindOf kindOf;                  // 시설 종류
        private String searchContent;

        @Builder
        public CenterSearchFilterDto(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent) {
            this.areas = areas;
            this.theme = theme;
            this.interestedAge = interestedAge;
            this.kindOf = kindOf;
            this.searchContent = searchContent;
        }

    }
}
