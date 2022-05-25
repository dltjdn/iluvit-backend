package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentationModifyResponseDto {

    Long presentationId;
    List<Long> ptDateId = new ArrayList<>();

    public PresentationModifyResponseDto(Presentation presentation) {
        presentationId = presentation.getId();
        presentation.getPtDates().forEach(ptDate -> ptDateId.add(ptDate.getId()));
    }
}
