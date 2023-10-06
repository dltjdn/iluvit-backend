package FIS.iLUVit.domain.parent.dto;

import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.center.domain.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParentDetailResponse {
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private Integer interestAge;
    private Theme theme;
    private String address;
    private String detailAddress;
    private String profileImg;
    private String name;

    public ParentDetailResponse(Parent parent, String profileImg) {
        this.nickname = parent.getNickName();
        this.phoneNumber = parent.getPhoneNumber();
        this.emailAddress = parent.getEmailAddress();
        this.interestAge = parent.getInterestAge();
        this.theme = parent.getTheme();
        this.address = parent.getAddress();
        this.detailAddress = parent.getDetailAddress();
        this.name = parent.getName();
        this.profileImg = profileImg;
    }
}
