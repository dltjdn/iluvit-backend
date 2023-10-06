package FIS.iLUVit.domain.alarm.dto;

import FIS.iLUVit.domain.alarm.domain.Accept;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ScheduleByDateResponse {
    private Long scheduleId;        // 스케줄 id
    private String aCode;           // 현장요원 코드
    private String aName;           // 현장요원 이름
    @JsonProperty("center_id")
    private Long centerId;          // 센터 id
    private String cName;           // 센터 이름
    private String cAddress;        // 센터 주소
    private String cPh;             // 센터 전화번호
    private Integer estimateNum;    // 예상 인원
    private LocalDate visitDate;    // 방분 날짜
    private LocalTime visitTime;    // 방문 시간
    private String centerEtc;       // 센터 특이사항
    private String agentEtc;        // 현장요원 특이사항
    private String modifiedInfo;    // 변경사항
    private String totalEtc;        // 스케쥴 특이사항
    private String callCheck;       // 최근 통화 상태
    private String callCheckInfo;   // 최근 통화 상태 정보(부재중 몇건 or 통화오류 이유)
    private Accept accept;          // 일정 수락 여부
}