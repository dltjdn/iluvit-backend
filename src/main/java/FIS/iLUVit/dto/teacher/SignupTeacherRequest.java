package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupTeacherRequest {
    @Size(min=5, message = "아이디는 5자 이상이어야합니다.")
    private String loginId;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&^])[A-Za-z\\d@$!%*#?&^]{8,}$", message = "비밀번호는 문자, 숫자, 특수문자를 최소 한개씩 포함한 8자 이상이어야합니다.")
    private String password;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String passwordCheck;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    @Size(max = 10)
    private String name;
    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하여야 합니다.")
    private String nickname;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String phoneNum;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String emailAddress;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String address;
    @NotNull(message = "입력하지 않은 목록이 있습니다.")
    private String detailAddress;
    private Long centerId;

    public Teacher createTeacher(Center center, String pwd){
        return Teacher.builder()
                .loginId(loginId)
                .password(pwd)
                .nickName(nickname)
                .name(name)
                .phoneNumber(phoneNum)
                .emailAddress(emailAddress)
                .address(address)
                .detailAddress(detailAddress)
                .center(center)
                .approval(Approval.WAITING)
                .auth(Auth.TEACHER)
                .readAlarm(true)
                .build();
    }

}
