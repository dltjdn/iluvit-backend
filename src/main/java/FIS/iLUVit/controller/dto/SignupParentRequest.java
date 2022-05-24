package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;

@Data
public class SignupParentRequest {
    private String loginId;
    private String password;
    private String passwordCheck;
    private String nickname;
    private String name;
    private String phoneNum;
    private String emailAddress;
    private Theme theme;
    private Integer interestAge;

    public Parent createParent() {
        return Parent.builder()
                .nickName(nickname)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNum)
                .hasProfileImg(null)
                .emailAddress(emailAddress)
                .name(name)
                .theme(theme)
                .interestAge(interestAge)
                .auth(Auth.PARENT)
                .build();
    }
}
