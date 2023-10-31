package FIS.iLUVit.domain.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Location {
    protected String sido;
    protected String sigungu;
    protected Double longitude;
    protected Double latitude;

    public static Location of(Pair<Double, Double> loAndLat, Pair<String, String> hangjung){
        return Location.builder()
                .sido(hangjung.getFirst())
                .sigungu(hangjung.getSecond())
                .longitude(loAndLat.getFirst())
                .latitude(loAndLat.getSecond())
                .build();
    }

}
