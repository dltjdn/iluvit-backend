package FIS.iLUVit.dto.presentation;

import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PtDateDetailDto {
    private Long ptDateId;              // 신청 아이디
    private LocalDate date;            // 설명회 날짜 시간
    private String time;
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    private Integer participantCnt;     // 신청 사람 수
    private Integer waitingCnt;         // 대기 수
    private Long participantId;
    private Boolean isParticipant;
    private Long waitingId;
    private Boolean isWaiting;

    public static PtDateDetailDto from(PtDate ptDate){
        return PtDateDetailDto.builder()
                .ptDateId(ptDate.getId())
                .date(ptDate.getDate())
                .time(ptDate.getTime())
                .ablePersonNum(ptDate.getAblePersonNum())
                .participantCnt(ptDate.getParticipantCnt())
                .waitingCnt(ptDate.getWaitingCnt())
                .build();
    }

    public static PtDateDetailDto of(PtDate ptDate, Participation participation, Waiting waiting) {
        PtDateDetailDto.PtDateDetailDtoBuilder builder = PtDateDetailDto.builder()
                .ptDateId(ptDate.getId())
                .date(ptDate.getDate())
                .time(ptDate.getTime())
                .ablePersonNum(ptDate.getAblePersonNum())
                .participantCnt(ptDate.getParticipantCnt())
                .waitingCnt(ptDate.getWaitingCnt());

        if (participation != null) {
            builder.participantId(participation.getId())
                    .isParticipant(true);
        }

        if (waiting != null) {
            builder.waitingId(waiting.getId())
                    .isWaiting(true);
        }

        return builder.build();
    }
}
