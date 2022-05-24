package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.embeddable.Theme;
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
}
