package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@OnDelete(action = OnDeleteAction.CASCADE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Teacher extends User {


    @Enumerated(EnumType.STRING)
    private Approval approval;              // 교사 승인 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    public static Teacher createTeacher(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Auth auth, Approval approval, Center center) {
        Teacher teacher = new Teacher();
        teacher.nickName = nickName;
        teacher.loginId = loginId;
        teacher.password = password;
        teacher.phoneNumber = phoneNumber;
        teacher.hasProfileImg = hasProfileImg;
        teacher.emailAddress = emailAddress;
        teacher.name = name;
        teacher.auth = auth;
        teacher.approval = approval;
        teacher.center = center;
        return teacher;
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }

}
