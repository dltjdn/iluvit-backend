package FIS.iLUVit.controller.dto;

import FIS.iLUVit.repository.dto.CenterBannerDto;
import lombok.Data;

@Data
public class CenterBannerResponseDto {
    private CenterBannerDto banner;

    public CenterBannerResponseDto(CenterBannerDto banner) {
        this.banner = banner;
    }
}
