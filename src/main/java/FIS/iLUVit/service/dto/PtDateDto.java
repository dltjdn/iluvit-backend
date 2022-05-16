package FIS.iLUVit.service.dto;

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

    public PtDateDto(PresentationWithPtDatesDto queryDto) {
        ptDateId = queryDto.getPtDateId();
        date = queryDto.getDate();
        time = queryDto.getTime();
        ablePersonNum = queryDto.getAblePersonNum();
        participantCnt = queryDto.getParticipantCnt();
        waitingCnt = queryDto.getWaitingCnt();
    }
}
