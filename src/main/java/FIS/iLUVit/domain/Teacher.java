package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.LoginTeacherResponse;
import FIS.iLUVit.controller.dto.TeacherInfoResponse;
import FIS.iLUVit.controller.dto.TeacherDetailRequest;
import FIS.iLUVit.controller.dto.UserInfoResponse;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.security.LoginResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

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
    public Teacher(Long id, String nickName, String loginId, String password, String phoneNumber, String emailAddress, String name, Auth auth, Approval approval, Center center, String address, String detailAddress, Boolean readAlarm) {
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
        this.approval = approval;
        this.center = center;
        this.readAlarm = readAlarm;
    }

    public static Teacher createTeacher(String nickName, String loginId, String password, String phoneNumber, String emailAddress, String name, Auth auth, Approval approval, Center center, String address, String detailAddress) {
        return Teacher.builder()
                .nickName(nickName)
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .emailAddress(emailAddress)
                .name(name)
                .approval(approval)
                .center(center)
                .auth(auth)
                .address(address)
                .detailAddress(detailAddress)
                .build();
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }

    public void updateDetail(TeacherDetailRequest request) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
    }

    public void updateDetailWithPhoneNum(TeacherDetailRequest request) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.phoneNumber = request.getPhoneNum();
    }

    public Teacher canWrite(Long centerId) {
        if(approval != Approval.ACCEPT || !Objects.equals(centerId, center.getId()))
            throw new CenterException(CenterErrorResult.AUTHENTICATION_FAILED);
        return this;
    }

    public Teacher canRead(Long centerId){
        if(approval != Approval.ACCEPT || !Objects.equals(centerId, center.getId()))
            throw new CenterException(CenterErrorResult.AUTHENTICATION_FAILED);
        return this;
    }

    @Override
    public LoginResponse getLoginInfo() {
        return new LoginTeacherResponse(this);
    }

    @Override
    public UserInfoResponse getUserInfo() {
        return new TeacherInfoResponse(id, nickName, auth, center, approval);
    }

    public void beDirector() {
        this.auth = Auth.DIRECTOR;
    }

    public void assignCenter(Center center) {
        this.center = center;
        this.approval = Approval.WAITING;
    }

    public void exitCenter() {
        this.center = null;
        this.auth = Auth.TEACHER;
    }

    public void acceptTeacher() {
        this.approval = Approval.ACCEPT;
    }

    public void beTeacher() {
        this.auth = Auth.TEACHER;
    }
}
