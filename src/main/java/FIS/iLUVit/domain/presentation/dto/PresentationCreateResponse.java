package FIS.iLUVit.domain.presentation.dto;

import FIS.iLUVit.domain.presentation.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentationCreateResponse {

    Long presentationId;
    List<Long> ptDateId = new ArrayList<>();

    public static PresentationCreateResponse of(Presentation presentation, List<Long> ptDateIds) {

        return PresentationCreateResponse.builder()
                .presentationId(presentation.getId())
                .ptDateId(ptDateIds)
                .build();
    }
}
