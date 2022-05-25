package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.UpdateTeacherDetailRequest;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PresentationException;
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

    public void updateDetail(UpdateTeacherDetailRequest request) {
        this.nickName = request.getNickname();
        this.phoneNumber = request.getPhoneNumber();
        this.emailAddress = request.getEmailAddress();
    }

    public void canWrite() {
        if(approval != Approval.ACCEPT)
            throw new PresentationException("시설에 대해 작성 권한이 없습니다.");
    }

    public void canRead(){
        if(approval != Approval.ACCEPT)
            throw new PresentationException("시설 상세정보 읽을 권한이 없습니다.");
    }
}
