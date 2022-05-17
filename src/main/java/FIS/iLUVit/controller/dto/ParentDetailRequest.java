package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ParentDetailRequest {
    private String nickname;
    private String emailAddress;
    private Integer interestAge;
    private Theme theme;
    private MultipartFile profileImg;
}
