package FIS.iLUVit.service.dto;

import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PtDateDto {

    private Long ptDateId;              // 신청 아이디
    private LocalDate date;            // 설명회 날짜 시간
    private String time;
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    private Integer participantCnt;     // 신청 사람 수
    private Integer waitingCnt;         // 대기 수
    private Boolean isParticipant;
    private Long participantId;
    private Boolean isWaiting;
    private Long waitingId;

    public PtDateDto(PresentationWithPtDatesDto queryDto) {
        ptDateId = queryDto.getPtDateId();
        date = queryDto.getDate();
        time = queryDto.getTime();
        ablePersonNum = queryDto.getAblePersonNum();
        participantCnt = queryDto.getParticipantCnt();
        waitingCnt = queryDto.getWaitingCnt();
        isParticipant = queryDto.getParticipationId() != null;
        participantId = queryDto.getParticipationId();
        isWaiting = queryDto.getWaitingId() != null;
        waitingId = queryDto.getWaitingId();
    }

    public PtDateDto(PtDate ptDate) {
        ptDateId = ptDate.getId();
        date = ptDate.getDate();
        time = ptDate.getTime();
        ablePersonNum = ptDate.getAblePersonNum();
        participantCnt = ptDate.getParticipantCnt();
        waitingCnt = ptDate.getWaitingCnt();
    }
}
