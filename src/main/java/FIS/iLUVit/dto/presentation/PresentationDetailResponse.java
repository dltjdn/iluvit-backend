package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.Presentation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresentationDetailResponse {
    private Long presentationId;
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;          // 설명회 신청 기간
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개
    private List<String> images = new ArrayList<>();
    List<PtDateDetailDto> ptDateDtos = new ArrayList<>();


    public PresentationDetailResponse(Presentation presentation, List<String> encodedInfoImage){
        presentationId = presentation.getId();
        startDate = presentation.getStartDate();
        endDate = presentation.getEndDate();
        place = presentation.getPlace();
        content = presentation.getContent();
        imgCnt = presentation.getImgCnt();
        videoCnt = presentation.getVideoCnt();
        images = encodedInfoImage;
        presentation.getPtDates().forEach(ptDate -> ptDateDtos.add(new PtDateDetailDto(ptDate)));
    }

    public static PresentationDetailResponse of(Presentation presentation, List<String> images, List<PtDateDetailDto> ptDateDtos){

        return PresentationDetailResponse.builder()
                .presentationId(presentation.getId())
                .startDate(presentation.getStartDate())
                .endDate(presentation.getEndDate())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .imgCnt(presentation.getImgCnt())
                .videoCnt(presentation.getVideoCnt())
                .images(images)
                .ptDateDtos(ptDateDtos)
                .build();

    }


}
