package FIS.iLUVit.dto.teacher;

import FIS.iLUVit.domain.iluvit.Teacher;
import lombok.Getter;

@Getter
public class TeacherDetailResponse {
    private String name;
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String detailAddress;
    private String profileImg;

    public TeacherDetailResponse(Teacher teacher, String profileImg) {
        this.name = teacher.getName();
        this.nickname = teacher.getNickName();
        this.phoneNumber = teacher.getPhoneNumber();
        this.emailAddress = teacher.getEmailAddress();
        this.address = teacher.getAddress();
        this.detailAddress = teacher.getDetailAddress();
        this.profileImg = profileImg;
    }
}
