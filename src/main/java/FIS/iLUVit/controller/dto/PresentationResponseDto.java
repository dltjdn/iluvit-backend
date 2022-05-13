package FIS.iLUVit.controller.dto;

import FIS.iLUVit.service.dto.PresentationQuryDto;
import FIS.iLUVit.service.dto.PtDateDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PresentationResponseDto {
    private Long presentationId;
    private LocalDate start_date;          // 설명회 신청 기간
    private LocalDate end_date;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개


    List<PtDateDto> ptDateDtos = new ArrayList<>();

    public PresentationResponseDto(PresentationQuryDto key, List<PtDateDto> value){
        this.presentationId = key.getPresentationId();
        this.start_date = key.getStart_date();
        this.end_date = key.getEnd_date();
        this.place = key.getPlace();
        this.content = key.getContent();
        this.imgCnt = key.getImgCnt();
        this.videoCnt = key.getVideoCnt();
        value.forEach(ptDateDto -> ptDateDtos.add(ptDateDto));
    }
}
