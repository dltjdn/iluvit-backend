package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.teacher.domain.Teacher;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherFindOneDetailResponse {
    private String name;
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String detailAddress;
    private String profileImg;

    public static TeacherFindOneDetailResponse from(Teacher teacher){
        return TeacherFindOneDetailResponse.builder()
                .name(teacher.getName())
                .nickname(teacher.getNickName())
                .phoneNumber(teacher.getPhoneNumber())
                .emailAddress(teacher.getEmailAddress())
                .address(teacher.getAddress())
                .detailAddress(teacher.getDetailAddress())
                .profileImg(teacher.getProfileImagePath())
                .build();
    }
}
