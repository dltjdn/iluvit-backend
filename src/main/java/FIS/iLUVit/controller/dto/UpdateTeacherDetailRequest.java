package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UpdateTeacherDetailRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String name;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String nickname;
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Boolean changePhoneNum;
    private String phoneNum;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String emailAddress;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String address;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String detailAddress;
    private MultipartFile profileImg;
}
