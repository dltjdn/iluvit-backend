package FIS.iLUVit;

import FIS.iLUVit.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class imageServiceTest {

    @Autowired
    ImageService imageService;

    String path = "/Users/hyeonseung-gu/Desktop/center/";
    String path1 = "/Users/hyeonseung-gu/Desktop/centerProfile/";

    @Test
    @DisplayName("폴더 생성")
    public void Folder() throws IOException {
//        imageService.mkDir(path);
//        imageService.getEncodedImage(path, 3);
//        imageService.clear(path);
        imageService.mkDir(path);
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(path, 5);
    }

    @Test
    public void list(){
        List<Long> integers = new ArrayList<>();
        integers.add(1L);
        integers.add(2L);
        integers.add(3L);
        imageService.getEncodedProfileImage(path1, integers);
    }
}
