package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationSaveResponseDto {

    Long presentationId;
    List<Long> ptDateId = new ArrayList<>();

    public PresentationSaveResponseDto(Presentation presentation) {
        presentationId = presentation.getId();
        presentation.getPtDates().forEach(ptDate -> ptDateId.add(ptDate.getId()));
    }
}
