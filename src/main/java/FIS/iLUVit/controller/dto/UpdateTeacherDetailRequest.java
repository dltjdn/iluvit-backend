package FIS.iLUVit.controller.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateTeacherDetailRequest {
    private String nickname;
    private Boolean changePhoneNum;
    private String phoneNum;
    private String emailAddress;
    private String address;
    private String detailAddress;
    private MultipartFile profileImg;
}
