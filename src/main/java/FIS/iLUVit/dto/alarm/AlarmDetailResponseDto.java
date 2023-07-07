package FIS.iLUVit.dto.alarm;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;

@AllArgsConstructor
public class AlarmDetailResponseDto {
    private String message;
    private Integer status;
    private Boolean success;
    private Slice<AlarmDetailDto> data;
}
