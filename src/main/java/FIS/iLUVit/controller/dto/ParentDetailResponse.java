package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public ParentDetailResponse(Parent parent) {
        this.nickname = parent.getNickName();
        this.phoneNumber = parent.getPhoneNumber();
        this.emailAddress = parent.getEmailAddress();
        this.interestAge = parent.getInterestAge();
        this.theme = parent.getTheme();
        this.address = parent.getAddress();
        this.detailAddress = parent.getDetailAddress();
    }
}
