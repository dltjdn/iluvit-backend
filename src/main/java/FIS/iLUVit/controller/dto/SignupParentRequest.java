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
    private String phoneNum;
    private String nickname;
    private String name;
    private String emailAddress;
    private String address;
    private String detailAddress;
    private Theme theme;
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
                .hasProfileImg(false)
                .theme(theme)
                .interestAge(interestAge)
                .auth(Auth.PARENT)
                .build();
    }
}
