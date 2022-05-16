package FIS.iLUVit.service.dto;

import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PresentationQuryDto {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개

    public PresentationQuryDto(PresentationWithPtDatesDto querydto) {
        presentationId = querydto.getPresentationId();
        startDate = querydto.getStartDate();
        endDate = querydto.getEndDate();
        place = querydto.getPlace();
        content = querydto.getContent();
        imgCnt = querydto.getImgCnt();
        videoCnt = querydto.getVideoCnt();
    }
}
