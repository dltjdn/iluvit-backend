package FIS.iLUVit.domain.teacher.domain;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.teacher.dto.TeacherSignupRequest;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.teacher.dto.TeacherLoginResponse;
import FIS.iLUVit.domain.teacher.dto.TeacherBasicInfoResponse;
import FIS.iLUVit.domain.teacher.dto.TeacherUpdateRequest;
import FIS.iLUVit.domain.user.dto.UserFindOneResponse;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.dto.UserLoginResponse;
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

    @Builder(access = AccessLevel.PRIVATE)
    public Teacher(String nickName, String loginId, String password, String phoneNumber, String emailAddress, String name, Auth auth, Approval approval,Location location, Center center, String address, String detailAddress, Boolean readAlarm) {
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
        this.location = location;
        this.center = center;
        this.readAlarm = readAlarm;
    }

    public static Teacher of(TeacherSignupRequest request, String password, Center center, Location location) {
        return Teacher.builder()
                .nickName(request.getNickname())
                .loginId(request.getLoginId())
                .password(password)
                .phoneNumber(request.getPhoneNum())
                .emailAddress(request.getEmailAddress())
                .name(request.getName())
                .approval(Approval.WAITING)
                .auth(Auth.TEACHER)
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .center(center)
                .readAlarm(true)
                .location(location)
                .build();
    }

    public void updateTeacherInfo(TeacherUpdateRequest request, Location location) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.location = location;
    }

    public void updateTeacherInfoWithPhoneNum(TeacherUpdateRequest request, Location location) {
        this.name = request.getName();
        this.nickName = request.getNickname();
        this.emailAddress = request.getEmailAddress();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.phoneNumber = request.getPhoneNum();
        this.location = location;
    }

    public Teacher checkPermission(Long centerId) {
        if(approval != Approval.ACCEPT || !Objects.equals(centerId, center.getId()))
            throw new UserException(UserErrorResult.FORBIDDEN_ACCESS);
        return this;
    }

    @Override
    public UserLoginResponse getLoginInfo() {
        return new TeacherLoginResponse(this);
    }

    @Override
    public UserFindOneResponse getUserInfo() {
        return new TeacherBasicInfoResponse(id, nickName, auth, center, approval);
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
