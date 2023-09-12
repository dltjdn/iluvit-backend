package FIS.iLUVit.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TeacherDetailRequest {
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String name;
    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하여야 합니다.")
    private String nickname;
    @NotNull(message = "입력되지 않은 목록이 있습니다.")
    private Boolean changePhoneNum;
    private String phoneNum;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String emailAddress;
    @NotEmpty(message = "입력되지 않은 목록이 있습니다.")
    private String address;
    @NotNull
    private String detailAddress;
    private MultipartFile profileImg;
}
