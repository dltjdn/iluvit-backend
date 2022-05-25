package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.UpdateTeacherDetailRequest;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PresentationException;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public Teacher(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Auth auth, Approval approval, Center center) {
        this.nickName = nickName;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.hasProfileImg = hasProfileImg;
        this.emailAddress = emailAddress;
        this.name = name;
        this.auth = auth;
        this.approval = approval;
        this.center = center;
    }

    public static Teacher createTeacher(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Auth auth, Approval approval, Center center) {
        return Teacher.builder()
                .nickName(nickName)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .hasProfileImg(hasProfileImg)
                .emailAddress(emailAddress)
                .name(name)
                .approval(approval)
                .center(center)
                .build();
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
