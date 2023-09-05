package FIS.iLUVit.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDeleteRequest {
    private List<Long> alarmIds;
}
