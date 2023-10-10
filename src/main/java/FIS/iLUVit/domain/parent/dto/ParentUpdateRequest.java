package FIS.iLUVit.domain.parent.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentUpdateRequest {
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    private String name;
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하여야 합니다.")
    private String nickname;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private Boolean changePhoneNum;
    private String phoneNum;
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    private String address;
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    private String detailAddress;
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    private String emailAddress;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private Integer interestAge;
    @NotEmpty(message = "입력하지 않은 목록이 있습니다.")
    private String theme;
    private MultipartFile profileImg;
}
