package FIS.iLUVit.domain.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CenterImageRequest {
    private MultipartFile profileImage;
    private List<MultipartFile> infoImages;
}
