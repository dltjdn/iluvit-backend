package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class imageServiceStubTest {


    ImageServiceStub imageServiceStub = new ImageServiceStub();
    ImageServiceStubAmazon imageServiceStubAmazon = new ImageServiceStubAmazon();
    MultipartFile multipartFile;
    List<MultipartFile> multipartFileList = new ArrayList<>();
    @BeforeEach
    public void init() throws IOException {
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);
    }


    @Test
    public void BaseImageEntity_imagePath_만들기() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        String destPath = imageServiceStub.getProfileDestPath(multipartFile, center);

        //then
        assertThat(destPath)
                .isEqualTo(imageServiceStub.path + '/' + "center/" + "1.png");
    }

    @Test
    public void 이미지_경로_만들기() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();
        //when
        List<String> destPath = imageServiceStub.getInfoDestPath(multipartFileList, center);

        //then
        System.out.println("destPath = " + destPath);
        assertThat(destPath.get(0))
                .isEqualTo(imageServiceStub.path + '/' + "centerInfo/1/" + "1.png");
    }

    @Test
    public void multipart가_null일_경우() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();
        //when
        String destPath = imageServiceStub.saveProfileImage(null, center);
        //then
        assertThat(destPath).isNull();
    }

    @Test
    public void 프로필_이미지_저장() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        String destPath = imageServiceStub.saveProfileImage(multipartFile, center);

        System.out.println("center.getProfileImagePath() = " + center.getProfileImagePath());
        //then
        assertThat(destPath).isNull();
        assertThat(center.getProfileImagePath())
                .isEqualTo("http://localhost:8081" + '/' + "center/" + "1.png");
    }

    @Test
    public void info_이미지_저장() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        String destPath = imageServiceStub.saveInfoImages(multipartFileList, center);

        System.out.println("center.getProfileImagePath() = " + center.getInfoImagePath());
        //then
        assertThat(destPath).isNull();
        assertThat(center.getInfoImagePath())
                .isEqualTo("http://localhost:8081" + '/' + "centerInfo/1/" + "1.png," + "http://localhost:8081" + '/' + "centerInfo/1/" + "2.png,");
    }



    @Test
    public void 아마존_이미지_경로_테스트() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        String destPath = imageServiceStubAmazon.getProfileDestPath(multipartFile, center);

        //then
        System.out.println("destPath = " + destPath);
        assertThat(destPath)
                .isEqualTo("centerProfile/" + "1.png");
    }

    @Test
    public void 아마존_이미지_경로_테스트2() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();
        //when
        List<String> destPath = imageServiceStubAmazon.getInfoDestPath(multipartFileList, center);

        //then
        System.out.println("destPath = " + destPath);
        assertThat(destPath.get(0))
                .isEqualTo("centerInfo/1/" + "1.png");
    }

    @Test
    public void 아마존_이미지_저장_profile() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        System.out.println("center.getProfileImagePath() = " + center.getProfileImagePath());
        //then
        assertThat(center.getProfileImagePath())
                .isEqualTo("https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerProfile/" + "1.png");
    }

    @Test
    public void 저장을_위해_S3_일부_키_삭제() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .infoImagePath("https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "1.png," +
                        "https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "2.png," +
                        "https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "3.png,")
                .build();

        imageServiceStubAmazon.saveInfoImages(multipartFileList, center);
        //then
        assertThat(center.getInfoImagePath())
                .isEqualTo("https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "1.png," +
                        "https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "2.png,");
    }

    @Test
    public void 아마존_이미지_저장_info() throws Exception {
        //given
        Center center = Center.builder()
                .id(1L)
                .build();

        //when
        imageServiceStubAmazon.saveInfoImages(multipartFileList, center);

        System.out.println("center.getInfoImagePath() = " + center.getInfoImagePath());
        //then
        assertThat(center.getInfoImagePath())
                .isEqualTo("https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "1.png," +
                        "https://iluvit.s3.ap-northeast-2.amazonaws.com" + '/' + "centerInfo/1/" + "2.png,");
    }
}
