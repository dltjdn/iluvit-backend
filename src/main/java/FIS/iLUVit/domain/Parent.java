package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.parameters.P;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Data
@OnDelete(action = OnDeleteAction.CASCADE)
public class Parent extends User {
    @OneToMany(mappedBy = "parent")
    private List<Child> children;

    @Embedded
    private Theme theme;                    // 테마 (학부모 관심사)

    private Integer interestAge;            // 관심나이

    public static Parent createParent(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Theme theme, Integer interestAge, Auth auth) {
        Parent parent = new Parent();
        parent.nickName = nickName;
        parent.loginId = loginId;
        parent.password = password;
        parent.phoneNumber = phoneNumber;
        parent.hasProfileImg = hasProfileImg;
        parent.emailAddress = emailAddress;
        parent.name = name;
        parent.theme = theme;
        parent.interestAge = interestAge;
        parent.auth = auth;
        return parent;
    }
}
