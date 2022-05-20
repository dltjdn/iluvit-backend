package FIS.iLUVit.controller.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateTeacherDetailRequest {
    private String nickname;
    private String phoneNumber;
    private String emailAddress;
    private MultipartFile profileImg;
}
