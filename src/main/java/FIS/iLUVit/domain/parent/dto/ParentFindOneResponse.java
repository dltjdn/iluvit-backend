package FIS.iLUVit.domain.parent.dto;

import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.center.domain.Theme;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ParentFindOneResponse {
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private Integer interestAge;
    private Theme theme;
    private String address;
    private String detailAddress;
    private String profileImg;
    private String name;

    public static ParentFindOneResponse from(Parent parent){
        return ParentFindOneResponse.builder()
                .nickname(parent.getNickName())
                .phoneNumber(parent.getPhoneNumber())
                .emailAddress(parent.getEmailAddress())
                .interestAge(parent.getInterestAge())
                .theme(parent.getTheme())
                .address(parent.getAddress())
                .detailAddress(parent.getDetailAddress())
                .name(parent.getName())
                .profileImg(parent.getProfileImagePath())
                .build();
    }


}
