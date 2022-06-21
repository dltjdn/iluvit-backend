package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentDetailRequest {
    private String nickname;
    private Boolean changePhoneNum;
    private String phoneNum;
    private String address;
    private String detailAddress;
    private String emailAddress;
    private Integer interestAge;
    private String theme;
    private MultipartFile profileImg;
}
