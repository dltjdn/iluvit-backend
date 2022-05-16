package FIS.iLUVit;

import FIS.iLUVit.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.MalformedURLException;

@SpringBootTest
public class imageServiceTest {

    @Autowired
    ImageService imageService;

    String path = "/Users/hyeonseung-gu/Desktop/center/";

    @Test
    @DisplayName("폴더 생성")
    public void Folder() throws IOException {
//        imageService.mkDir(path);
//        imageService.getEncodedImage(path, 3);
        imageService.clear(path);
    }

}
