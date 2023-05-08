package FIS.iLUVit.service;

import FIS.iLUVit.domain.BaseImageEntity;
import FIS.iLUVit.exception.ImageErrorResult;
import FIS.iLUVit.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * s3 testStub
 */
public class ImageServiceStubAmazon implements ImageService {
    String prefix = "https://iluvit.s3.ap-northeast-2.amazonaws.com/";

    /**
     * entity 의 아이디 값 추출
     */
    protected Long abstractEntityId(BaseImageEntity entity) {
        try {
            Class<?> clazz = entity.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            while (true) {
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


    public void saveInfoImages(List<MultipartFile> images, BaseImageEntity entity) {
        // null 이거나 비어있다면 return
        if (images == null || images.size() == 0) {
            return;
        }
        // 이미지 분석 단계
        List<String> destPaths = getInfoDestPath(images, entity);

        // 이미지 저장 로직 + entity 업데이트 추가
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String destPath = destPaths.get(i);
            saveImage(image, destPath);
        }
        clear(getInfoDeleteKey(entity, destPaths));
        updateInfoImagePath(entity, destPaths);


    }

    private void clear(Set<String> deleteKeys) {
        deleteKeys.forEach(
                deleteKey -> {
                    System.out.println("deleteKey 로 이미지를 삭제합니다. " + deleteKey);
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
        String temp = "";
        for (String destPath : list) {
            temp += destPath + ',';
        }
        entity.updateInfoImagePath(imgCnt, temp);
    }

    public void saveProfileImage(MultipartFile image, BaseImageEntity entity) {
        // null 이거나 비어있다면 return
        if (image == null || image.isEmpty()) {
            entity.updateProfileImagePath("basic");
        } else {
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
        System.out.println("============= destPath key로 이미지를 저장합니다 ===============");
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String extractFileName(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(0, pos);
    }

    public void mkDirForS3() {

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
        if (profileImagePath == null)
            return null;
        return profileImagePath;
    }

    public List<String> getInfoImages(String infoImagePath) {
        if (infoImagePath == null || infoImagePath.equals(""))
            return new ArrayList<>();
        return List.of(infoImagePath.split(","));
    }

}
