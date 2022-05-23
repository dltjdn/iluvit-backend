package FIS.iLUVit.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PresentationWithPtDatesDto {
    private Long presentationId;
    private LocalDate startDate;          // 설명회 신청 기간
    private LocalDate endDate;
    private String place;               // 설명회 장소
    private String content;             // 설명회 내용
    private Integer imgCnt;             // 설명회 이미지 개수 최대 __장
    private Integer videoCnt;           // 설명회 동영상 개수 최대 _개

    private Long ptDateId;              // 신청 아이디
    private LocalDate date;            // 설명회 날짜 시간
    private String time;
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    private Integer participantCnt;     // 신청 사람 수
    private Integer waitingCnt;         // 대기 수
}
