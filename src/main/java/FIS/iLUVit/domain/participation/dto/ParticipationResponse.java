package FIS.iLUVit.domain.participation.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.participation.domain.Participation;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import FIS.iLUVit.domain.participation.domain.Status;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ParticipationResponse {
    private Long parentId;
    private Long participantId;
    private Long waitingId;
    private Long ptDateId;
    private Long presentationId;
    private Long centerId;
    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    private LocalDate presentationDate;
    private String time;            // 설명회 날짜 시간
    private String centerProfileImage;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private String centerName;
    private String tel;                     // 전화번호
    private String address;                 // 주소
    private String addressDetail;
    private Integer ablePersonNum;
    private Integer participantCnt;
    private Status status;

    public static ParticipationResponse from(Participation participation) {
        PtDate ptDate = participation.getPtDate();
        Parent parent = participation.getParent();
        Presentation presentation = ptDate.getPresentation();
        Center center = presentation.getCenter();

        return ParticipationResponse.builder()
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
                .addressDetail(center.getAddressDetail())
                .centerProfileImage(center.getProfileImagePath())
                .ablePersonNum(ptDate.getAblePersonNum())
                .participantCnt(ptDate.getParticipantCnt())
                .status(participation.getStatus())
                .build();
    }

    public static ParticipationResponse of(Waiting waiting){
        PtDate ptDate = waiting.getPtDate();
        Parent parent = waiting.getParent();
        Presentation presentation = ptDate.getPresentation();
        Center center = presentation.getCenter();

        return ParticipationResponse.builder()
                .parentId(parent.getId())
                .waitingId(waiting.getId())
                .ptDateId(ptDate.getId())
                .presentationId(presentation.getId())
                .centerId(center.getId())
                .presentationDate(ptDate.getDate())
                .time(ptDate.getTime())
                .place(presentation.getPlace())
                .content(presentation.getContent())
                .centerName(center.getName())
                .centerProfileImage(center.getProfileImagePath())
                .tel(center.getTel())
                .address(center.getAddress())
                .addressDetail(center.getAddressDetail())
                .ablePersonNum(ptDate.getAblePersonNum())
                .participantCnt(ptDate.getParticipantCnt())
                .status(Status.WAITING)
                .build();
    }

}
