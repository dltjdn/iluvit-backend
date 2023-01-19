package FIS.iLUVit.service;

import FIS.iLUVit.domain.BaseImageEntity;
import FIS.iLUVit.exception.ImageErrorResult;
import FIS.iLUVit.exception.ImageException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@RequiredArgsConstructor
@Service
@Primary
public class S3ImageService implements ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.prefix}")
    private String prefix;

    private final AmazonS3Client amazonS3Client;

    @PostConstruct
    public void post(){
        System.out.println("path = " + bucket);
        System.out.println("prefix = " + prefix);
    }

    /**
     * entity 의 아이디 값 추출
     */
    protected Long abstractEntityId(BaseImageEntity entity) {
        try {
            Class<?> clazz = entity.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            while(true) {
                for (Field field : declaredFields) {
                    Annotation annotation = field.getAnnotation(Id.class);
                    if (annotation != null) {
                        field.setAccessible(true);
                        Long entityId = (Long) field.get(entity);
                        return entityId;
                    }
                }
                clazz = clazz.getSuperclass();
                declaredFields = clazz.getDeclaredFields();
            }
        } catch (IllegalAccessException exception) {
            throw new ImageException(ImageErrorResult.IMAGE_ANALYZE_FAILED);
        }
    }

    /**
     * 엔티티 class 이름 추출
     */
    protected String abstractEntityName(Class<? extends BaseImageEntity> clazz) {
        String absoluteName = clazz.getName();
        String[] split = absoluteName.split("[.]");
        String entityName = split[split.length - 1];
        return entityName.toLowerCase();
    }

    /**
     * 이미지 폴더 완성 시키기
     */
    protected String getInfoFolder(BaseImageEntity entity) {
        String className = abstractEntityName(entity.getClass());
        Long entityId = abstractEntityId(entity);
        String folder = className + "Info" + '/' + entityId + '/';
        return folder;
    }

    protected String getProfileFolder(BaseImageEntity entity) {
        String className = abstractEntityName(entity.getClass());
        String folder = className + "Profile" + '/';
        return folder;
    }

    public List<String> getInfoDestPath(List<MultipartFile> multipartFiles, BaseImageEntity entity) {
        String infoFolderPath = getInfoFolder(entity);
        int size = multipartFiles.size();
        List<String> infoDestPath = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            MultipartFile multipartFile = multipartFiles.get(i - 1);
            String ext = extractExt(multipartFile.getOriginalFilename());
            infoDestPath.add(infoFolderPath + i + '.' + ext);
        }
        return infoDestPath;
    }

    public String getProfileDestPath(MultipartFile multipartFile, BaseImageEntity entity) {
        String profileFolderPath = getProfileFolder(entity);
        Long entityId = abstractEntityId(entity);
        String ext = extractExt(multipartFile.getOriginalFilename());
        return profileFolderPath + entityId + '.' + ext;
    }


    public String saveInfoImages(List<MultipartFile> images, BaseImageEntity entity) {
        // null 이거나 비어있다면 return
        System.out.println("++++++++++++++++++"+images);
        if (images == null || images.size() == 0) {

            entity.updateInfoImagePath(0, null);
            return null;
        }
        // 이미지 분석 단계
        List<String> destPaths = getInfoDestPath(images, entity);
        System.out.println("+++++++++++++++++++++++++"+destPaths);

        // 이미지 저장 로직 + entity 업데이트 추가
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String destPath = destPaths.get(i);
            saveImage(image, destPath);
        }
        clear(getInfoDeleteKey(entity, destPaths));
        updateInfoImagePath(entity, destPaths);

        return null;
    }

    private void clear(Set<String> deleteKeys) {
        deleteKeys.forEach(
                deleteKey -> {
                    amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, deleteKey));
                }
        );
    }

    private void updateInfoImagePath(BaseImageEntity entity, List<String> destPaths) {
        List<String> list = new ArrayList<>();
        destPaths.forEach(destPath -> {
            // S3 에 맞게 조정
            list.add(prefix + destPath);
        });

        Integer imgCnt = destPaths.size();
        StringBuilder temp2 = new StringBuilder();
        for (String destPath : list) {
            temp2.append(destPath).append(",");
        }
        if(temp2.length() > 0) {
            temp2.deleteCharAt(temp2.length() - 1);
        }
        String temp = temp2.toString();
        entity.updateInfoImagePath(imgCnt, temp);

    }

    public void saveProfileImage(MultipartFile image, BaseImageEntity entity) {
        //null 이거나 비어있다면 return
        if(image == null || image.isEmpty()){
            entity.updateProfileImagePath("basic");
        }else{
            // image fullpath 완성
            String destPath = getProfileDestPath(image, entity);

            // 이미지 저장 로직 + entity 업데이트 추가 작업 필요
            saveImage(image, destPath);
            updateProfileImagePath(entity, destPath);
        }
    }

    private void updateProfileImagePath(BaseImageEntity entity, String destPath) {
        entity.updateProfileImagePath(prefix + destPath);
    }

    private void saveImage(MultipartFile image, String destPath) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());
        try (InputStream inputStream = image.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, destPath, inputStream, objectMetadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw new ImageException(ImageErrorResult.IMAGE_SAVE_FAILED);
        }
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String extractFileName(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(0, pos);
    }

    public Set<String> getInfoDeleteKey(BaseImageEntity entity, List<String> destPaths) {
        String infoImagePath = entity.getInfoImagePath();
        if (infoImagePath == null || infoImagePath.equals(""))
            return new HashSet<>();
        Set<String> targetKeys = new HashSet<>(List.of(infoImagePath.replace(prefix, "").split(",")));
        targetKeys.removeAll(destPaths);
        return targetKeys;
    }

    public String getProfileImage(BaseImageEntity entity) {
        String profileImagePath = entity.getProfileImagePath();
        if (profileImagePath == null)
            return null;
        return profileImagePath;
    }

    public List<String> getInfoImages(BaseImageEntity entity) {
        String infoImagePath = entity.getInfoImagePath();
        if (infoImagePath == null || infoImagePath.equals(""))
            return new ArrayList<>();
        return List.of(infoImagePath.split(","));
    }

    public String getProfileImage(String profileImagePath) {
        if(profileImagePath == null)
            return null;
        return profileImagePath;
    }

    public List<String> getInfoImages(String infoImagePath){
        if(infoImagePath == null || infoImagePath.equals(""))
            return new ArrayList<>();
        return List.of(infoImagePath.split(","));
    }

}
