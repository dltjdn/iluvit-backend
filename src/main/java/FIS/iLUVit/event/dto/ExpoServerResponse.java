package FIS.iLUVit.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExpoServerResponse {

    private List<ExpoResponse> data;

}
