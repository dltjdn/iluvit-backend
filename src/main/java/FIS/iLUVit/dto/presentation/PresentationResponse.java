package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentationResponse {

    Long presentationId;
    List<Long> ptDateId = new ArrayList<>();

    public PresentationResponse(Presentation presentation) {
        presentationId = presentation.getId();
        presentation.getPtDates().forEach(ptDate -> ptDateId.add(ptDate.getId()));
    }
}
