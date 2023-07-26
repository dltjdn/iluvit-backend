package FIS.iLUVit.dto.alarm;

import FIS.iLUVit.domain.enumtype.Accept;
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
    private Long schedule_id;       // 스케쥴 id
    private String a_code;          // 현장요원 코드
    private String a_name;          // 현장요원 이름
    private Long center_id;         // 센터 id
    private String c_name;          // 센터 이름
    private String c_address;       // 센터 주소
    private String c_ph;            // 센터 전화번호
    private Integer estimate_num;   // 예상 인원
    private LocalDate visit_date;   // 방분 날짜
    private LocalTime visit_time;   // 방문 시간
    private String center_etc;      // 센터 특이사항
    private String agent_etc;       // 현장요원 특이사항
    private String modified_info;   // 변경사항
    private String total_etc;       // 스케쥴 특이사항
    private String call_check;      // 최근 통화 상태
    private String call_check_info; // 최근 통화 상태 정보(부재중 몇건 or 통화오류 이유)
    private Accept accept;          //일정 수락 여부
}