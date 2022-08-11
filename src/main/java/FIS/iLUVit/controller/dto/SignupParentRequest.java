package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupParentRequest {
    @Size(min=5, message = "아이디는 5자 이상이어야합니다.")
    private String loginId;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String password;
    @NotNull
    private String passwordCheck;
    @NotNull
    private String phoneNum;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
    @Email
    private String emailAddress;
    @NotNull
    private String address;
    @NotNull
    private String detailAddress;
    private Theme theme;
    @NotNull
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
                .build();
    }
}
