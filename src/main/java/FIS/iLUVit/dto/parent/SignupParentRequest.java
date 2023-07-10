package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupParentRequest {
    @Size(min=5, message = "아이디는 5자 이상이어야합니다.")
    private String loginId;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String password;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String passwordCheck;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String phoneNum;
    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하여야 합니다.")
    private String nickname;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String name;
    @Email(message = "유효하지 않은 이메일 주소입니다.")
    private String emailAddress;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String address;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String detailAddress;
    private Theme theme;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    @JsonProperty("interestAge")
    private Integer interestAge;

    public Parent createParent(String pwd) {
        return Parent.builder()
                .loginId(loginId)
                .password(pwd)
                .name(name)
                .nickName(nickname)
                .phoneNumber(phoneNum)
                .emailAddress(emailAddress)
                .address(address)
                .detailAddress(detailAddress)
                .theme(theme)
                .interestAge(interestAge)
                .auth(Auth.PARENT)
                .readAlarm(true)
                .build();
    }
}
