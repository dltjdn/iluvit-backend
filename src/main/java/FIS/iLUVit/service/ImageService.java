package FIS.iLUVit.service;

import FIS.iLUVit.domain.BaseImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {


    String saveInfoImages(List<MultipartFile> images, BaseImageEntity entity);

    void saveProfileImage(MultipartFile image, BaseImageEntity entity);

    String getProfileImage(BaseImageEntity entity);

    List<String> getInfoImages(BaseImageEntity entity);

    String getProfileImage(String profileImagePath);

    List<String> getInfoImages(String infoImagePath);

}
