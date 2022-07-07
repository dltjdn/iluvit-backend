package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyParticipationsDto {
    private Long parentId;
    private Long participantId;
    private Long waitingId;
    private Long ptDateId;
    private Long presentationId;
    private Long centerId;
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate presentationDate;
    private String time;            // 설명회 날짜 시간
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private String centerName;
    private String tel;                     // 전화번호
    private String address;                 // 주소
    private Status status;

    public static MyParticipationsDto createDto(Participation participation) {
        PtDate ptDate = participation.getPtDate();
        Parent parent = participation.getParent();
        Presentation presentation = ptDate.getPresentation();
        Center center = presentation.getCenter();
        return MyParticipationsDto.builder()
                .parentId(parent.getId())
                .participantId(participation.getId())
                .ptDateId(ptDate.getId())
                .presentationId(presentation.getId())
                .centerId(center.getId())
                .presentationDate(ptDate.getDate())
                .time(ptDate.getTime())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .centerName(center.getName())
                .tel(center.getTel())
                .address(center.getAddress())
                .status(participation.getStatus())
                .build();
    }

    public static MyParticipationsDto createDto(Waiting waiting){
        PtDate ptDate = waiting.getPtDate();
        Parent parent = waiting.getParent();
        Presentation presentation = ptDate.getPresentation();
        Center center = presentation.getCenter();
        return MyParticipationsDto.builder()
                .parentId(parent.getId())
                .participantId(waiting.getId())
                .ptDateId(ptDate.getId())
                .presentationId(presentation.getId())
                .centerId(center.getId())
                .presentationDate(ptDate.getDate())
                .time(ptDate.getTime())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .centerName(center.getName())
                .tel(center.getTel())
                .address(center.getAddress())
                .status(Status.WAITING)
                .build();
    }

}
