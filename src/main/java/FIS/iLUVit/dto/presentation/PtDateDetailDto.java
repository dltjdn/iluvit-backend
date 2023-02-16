package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.PtDate;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class PtDateDetailDto {
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

    public PtDateDetailDto(PresentationWithPtDatesDto queryDto) {
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

    public PtDateDetailDto(PtDate ptDate) {
        ptDateId = ptDate.getId();
        date = ptDate.getDate();
        time = ptDate.getTime();
        ablePersonNum = ptDate.getAblePersonNum();
        participantCnt = ptDate.getParticipantCnt();
        waitingCnt = ptDate.getWaitingCnt();
    }
}
