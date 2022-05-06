package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
public class Parent extends User {
    @OneToMany(mappedBy = "parent")
    private List<Child> children;

    @Embedded
    private Theme theme;                    // 테마 (학부모 관심사)

    private Integer interestAge;            // 관심나이

    public static Parent createParent(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Theme theme, Integer interestAge) {
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
        return parent;
    }
}
