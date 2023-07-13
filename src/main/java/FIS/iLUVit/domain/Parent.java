package FIS.iLUVit.domain;

import FIS.iLUVit.dto.parent.LoginParentResponse;
import FIS.iLUVit.dto.parent.ParentDetailRequest;
import FIS.iLUVit.dto.parent.ParentBasicInfoDto;
import FIS.iLUVit.dto.user.UserBasicInfoDto;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.security.LoginResponse;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class Parent extends User {

    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<>();

    @Embedded
    private Theme theme;                    // 테마 (학부모 관심사)

    private Integer interestAge;            // 관심나이

    @OneToMany(mappedBy = "parent")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "parent")
    private List<Waiting> waitings = new ArrayList<>();

    @Builder
    public Parent(Long id, String nickName, String loginId, String password, String phoneNumber, String emailAddress, String name, Theme theme, Integer interestAge, Auth auth, String address, String detailAddress, Boolean readAlarm) {
        this.id = id;
        this.nickName = nickName;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.auth = auth;
        this.theme = theme;
        this.interestAge = interestAge;
        this.readAlarm = readAlarm;
    }

    public void updateDetail(ParentDetailRequest request, Theme theme) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.interestAge = request.getInterestAge();
        this.theme = theme;
    }

    public void updateDetailWithPhoneNum(ParentDetailRequest request, Theme theme) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.phoneNumber = request.getPhoneNum();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.interestAge = request.getInterestAge();
        this.theme = theme;
    }

    @Override
    public LoginResponse getLoginInfo() {
        return new LoginParentResponse(this);
    }

    @Override
    public UserBasicInfoDto getUserInfo() {
        return new ParentBasicInfoDto(id, nickName, auth);
    }



}
