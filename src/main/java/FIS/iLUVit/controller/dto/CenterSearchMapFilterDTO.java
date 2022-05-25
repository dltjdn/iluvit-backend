package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CenterSearchMapFilterDTO {
    private double longitude;
    private double latitude;
    private Theme theme;
    private Integer interestedAge;
    private KindOf kindOf;                  // 시설 종류
    private Integer distance;
}
