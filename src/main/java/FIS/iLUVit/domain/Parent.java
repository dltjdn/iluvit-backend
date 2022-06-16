package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.LoginParentResponse;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.filter.LoginResponse;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Data
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class Parent extends User {

    @OneToMany(mappedBy = "parent")
    private List<Child> children;

    @Embedded
    private Theme theme;                    // 테마 (학부모 관심사)

    private Integer interestAge;            // 관심나이

    @OneToMany(mappedBy = "parent")
    private List<Participation> participations = new ArrayList<>();


    @Builder
    public Parent(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Theme theme, Integer interestAge, Auth auth) {
        this.nickName = nickName;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.hasProfileImg = hasProfileImg;
        this.emailAddress = emailAddress;
        this.name = name;
        this.theme = theme;
        this.interestAge = interestAge;
        this.auth = auth;
    }

    public static Parent createParent(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Theme theme, Integer interestAge, Auth auth) {
        return Parent.builder()
                .nickName(nickName)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .hasProfileImg(hasProfileImg)
                .emailAddress(emailAddress)
                .name(name)
                .theme(theme)
                .interestAge(interestAge)
                .auth(auth)
                .build();
    }

    public void updateDetail(ParentDetailRequest request, Theme theme) {
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.interestAge = request.getInterestAge();
        this.theme = theme;
    }

    @Override
    public LoginResponse getUserInfo() {
        return new LoginParentResponse(id, nickName, auth, interestAge);
    }
}
