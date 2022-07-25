package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.LoginTeacherResponse;
import FIS.iLUVit.controller.dto.UpdateTeacherDetailRequest;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.filter.LoginResponse;
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
    public Teacher(Long id, String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Auth auth, Approval approval, Center center, String address, String detailAddress) {
        this.id = id;
        this.nickName = nickName;
        this.loginId = loginId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.hasProfileImg = hasProfileImg;
        this.emailAddress = emailAddress;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.auth = auth;
        this.approval = approval;
        this.center = center;
    }

    public static Teacher createTeacher(String nickName, String loginId, String password, String phoneNumber, Boolean hasProfileImg, String emailAddress, String name, Auth auth, Approval approval, Center center, String address, String detailAddress) {
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
                .auth(auth)
                .address(address)
                .detailAddress(detailAddress)
                .build();
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }

    public void updateDetail(UpdateTeacherDetailRequest request) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
    }

    public void updateDetailWithPhoneNum(UpdateTeacherDetailRequest request) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.phoneNumber = request.getPhoneNum();
    }

    public Teacher canWrite(Long centerId) {
        if(approval != Approval.ACCEPT || centerId != center.getId())
            throw new CenterException(CenterErrorResult.AUTHENTICATION_FAILED);
        return this;
    }

    public Teacher canRead(Long centerId){
        if(approval != Approval.ACCEPT || centerId != center.getId())
            throw new CenterException(CenterErrorResult.AUTHENTICATION_FAILED);
        return this;
    }

    @Override
    public LoginResponse getUserInfo() {
        return new LoginTeacherResponse(id, nickName, auth, center);
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
}
