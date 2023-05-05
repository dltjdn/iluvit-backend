package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.iluvit.Presentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
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
