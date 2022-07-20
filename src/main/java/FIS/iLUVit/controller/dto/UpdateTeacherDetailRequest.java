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
    @NotEmpty
    private String nickname;
    @NotEmpty
    private Boolean changePhoneNum;
    private String phoneNum;
    @NotEmpty
    private String emailAddress;
    @NotEmpty
    private String address;
    @NotEmpty
    private String detailAddress;
    @NotEmpty
    private MultipartFile profileImg;
}
