package FIS.iLUVit.domain.parent.domain;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.parent.dto.ParentCreateRequest;
import FIS.iLUVit.domain.participation.domain.Participation;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import FIS.iLUVit.domain.parent.dto.ParentUpdateRequest;
import FIS.iLUVit.domain.user.dto.UserFindOneResponse;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.user.dto.UserLoginResponse;
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
    public Parent(String nickName, String loginId, String password, String phoneNumber, String emailAddress, String name, Theme theme, Integer interestAge, Auth auth, String address, String detailAddress, Boolean readAlarm, Location location) {
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
        this.location = location;
    }

    public static Parent of(ParentCreateRequest request, String password, Location location){
        return Parent.builder()
                .nickName(request.getNickname())
                .loginId(request.getLoginId())
                .password(password)
                .phoneNumber(request.getPhoneNum())
                .emailAddress(request.getEmailAddress())
                .name(request.getName())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .auth(Auth.PARENT)
                .readAlarm(true)
                .location(location)
                .build();
    }

    public void updateParentInfo(ParentUpdateRequest request, Theme theme, Location location) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.interestAge = request.getInterestAge();
        this.theme = theme;
        this.location = location;
    }

    public void updateParentInfoWithPhoneNum(ParentUpdateRequest request, Theme theme, Location location) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.phoneNumber = request.getPhoneNum();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.interestAge = request.getInterestAge();
        this.theme = theme;
        this.location = location;
    }

    @Override
    public UserLoginResponse getLoginInfo() {
        return new UserLoginResponse(this);
    }

    @Override
    public UserFindOneResponse getUserInfo() {
        return new UserFindOneResponse(id, nickName, auth);
    }

}
