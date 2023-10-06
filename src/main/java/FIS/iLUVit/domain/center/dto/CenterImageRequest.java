package FIS.iLUVit.domain.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CenterImageRequest {
    private MultipartFile profileImage;
    private List<MultipartFile> infoImages;
}
