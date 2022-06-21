package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Teacher;
import lombok.Data;

@Data
public class TeacherDetailResponse {
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String detailAddress;
    private String profileImg;

    public TeacherDetailResponse(Teacher teacher) {
        this.nickname = teacher.getNickName();
        this.phoneNumber = teacher.getPhoneNumber();
        this.emailAddress = teacher.getEmailAddress();
        this.address = teacher.getAddress();
        this.detailAddress = teacher.getDetailAddress();
    }
}
