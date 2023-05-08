package FIS.iLUVit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Location {
    protected String sido;
    protected String sigungu;
    protected Double longitude;
    protected Double latitude;

    public Location(Pair<Double, Double> loAndLat, Pair<String, String> hangjung) {
        sido = hangjung.getFirst();
        sigungu = hangjung.getSecond();
        longitude = loAndLat.getFirst();
        latitude = loAndLat.getSecond();
    }
}
