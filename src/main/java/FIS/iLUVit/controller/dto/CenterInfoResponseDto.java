package FIS.iLUVit.controller.dto;

import FIS.iLUVit.repository.dto.CenterInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CenterInfoResponseDto {
    private CenterInfoDto centerInfo;
}
