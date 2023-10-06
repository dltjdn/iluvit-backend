package FIS.iLUVit.domain.alarm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ExpoServerResponse {
    private List<ExpoResponse> expoResponseList;
}
