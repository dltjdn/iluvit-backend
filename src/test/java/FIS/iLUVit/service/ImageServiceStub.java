package FIS.iLUVit.service;

import FIS.iLUVit.domain.BaseImageEntity;
import FIS.iLUVit.exception.ImageErrorResult;
import FIS.iLUVit.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Id;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * local testStub
 */
public class ImageServiceStub {

    public final String path = (System.getProperty("user.home") + "/Desktop").replace("\\", "/");
    protected final String prefix = "http://localhost:8081";

    /**
     * entity 의 아이디 값 추출
     */
    protected Long abstractEntityId(BaseImageEntity entity) {
        try {
            Class<? extends BaseImageEntity> clazz = entity.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Annotation annotation = field.getAnnotation(Id.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Long entityId = (Long) field.get(entity);
                    return entityId;
                }
            }
        } catch (IllegalAccessException exception) {
            throw new ImageException(ImageErrorResult.IMAGE_ANALYZE_FAILED);
        }
        return null;
    }

    /**
     * 엔티티 class 이름 추출
     */
    protected String abstractEntityName(Class<? extends BaseImageEntity> clazz){
        String absoluteName = clazz.getName();
        String[] split = absoluteName.split("[.]");
        String entityName = split[split.length - 1];
        return entityName.toLowerCase();
    }

    /**
     * 이미지 폴더 완성 시키기
     */
    protected String getInfoFolder(BaseImageEntity entity){
        String className = abstractEntityName(entity.getClass());
        Long entityId = abstractEntityId(entity);
        String folder = path + '/' + className + "Info" + '/' + entityId + '/';
        mkDir(folder);
        clear(folder);
        return folder;
    }

    protected String getProfileFolder(BaseImageEntity entity){
        String className = abstractEntityName(entity.getClass());
        String folder = path + '/' + className + '/';
        mkDir(folder);
        return folder;
    }

    public List<String> getInfoDestPath(List<MultipartFile> multipartFiles ,BaseImageEntity entity){
        String infoFolderPath = getInfoFolder(entity);
        int size = multipartFiles.size();
        List<String> infoDestPath = new ArrayList<>();
        for(int i = 1; i<= size; i++){
            MultipartFile multipartFile = multipartFiles.get(i - 1);
            String ext = extractExt(multipartFile.getOriginalFilename());
            infoDestPath.add(infoFolderPath + i + '.' + ext);
        }
        return infoDestPath;
    }

    public String getProfileDestPath(MultipartFile multipartFile, BaseImageEntity entity){
        String profileFolderPath = getProfileFolder(entity);
        Long entityId = abstractEntityId(entity);
        String ext = extractExt(multipartFile.getOriginalFilename());
        return profileFolderPath + entityId + '.' + ext;
    }


    public String saveInfoImages(List<MultipartFile> images, BaseImageEntity entity) {
        // null 이거나 비어있다면 return
        if(images == null || images.size() == 0)
            return null;
        // 이미지 분석 단계
        List<String> destPaths = getInfoDestPath(images, entity);

        // 이미지 저장 로직 + entity 업데이트 추가
        for(int i = 0; i < images.size(); i++){
            MultipartFile image = images.get(i);
            String destPath = destPaths.get(i);
            saveImage(image, destPath);
        }
        updateInfoImagePath(entity, destPaths);

        return null;
    }

    private void updateInfoImagePath(BaseImageEntity entity, List<String> destPaths) {
        List<String> list = new ArrayList();
        destPaths.forEach(destPath -> {
            // 보안을 위한 추상화 작업 실시
            destPath = destPath.replace(path, "");
            list.add(prefix + destPath);
        });

        Integer imgCnt = destPaths.size();
        String temp = "";
        for (String destPath : list) {
            temp += destPath + ',';
        }
        entity.updateInfoImagePath(imgCnt, temp);
    }

    public String saveProfileImage(MultipartFile image, BaseImageEntity entity) {
        // null 이거나 비어있다면 return
        if(image == null || image.isEmpty())
            return null;
        // image fullpath 완성
        String destPath = getProfileDestPath(image, entity);

        // 이미지 저장 로직 + entity 업데이트 추가 작업 필요
        saveImage(image, destPath);
        updateProfileImagePath(entity, destPath);
        return null;
    }

    private void updateProfileImagePath(BaseImageEntity entity, String destPath) {
        String temp = destPath.replace(path, "");
        entity.updateProfileImagePath(prefix + temp);
    }

    private void saveImage(MultipartFile image, String destPath) {
            System.out.println("============= destPath key로 이미지를 저장합니다 ===============");
    }

    public void mkDir(String destPath){
        String[] names = destPath.split("/");
        String temp = "/";
        for(int i = 1; i < names.length; i++) {
            temp += names[i] + "/";
            System.out.println("temp 폴더 접근= " + temp);
            File folder = new File(temp);
            if(!folder.exists()){
                try {
                    folder.mkdir();
                    System.out.println("현재 폴더 생성 완료 " + temp);
                } catch (Exception exception){
                    exception.printStackTrace();
                    throw new ImageException(ImageErrorResult.IMAGE_DIRECTORY_CREATE_FAILED);
                }
            }
        }
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String extractFileName(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(0, pos);
    }

    public void mkDirForS3(){

    }

    public String getProfileImage(BaseImageEntity entity) {
        String profileImagePath = entity.getProfileImagePath();
        if(profileImagePath == null)
            return null;
        return profileImagePath;
    }

    public List<String> getInfoImages(BaseImageEntity entity){
        String infoImagePath = entity.getInfoImagePath();
        if(infoImagePath == null || infoImagePath.equals(""))
            return new ArrayList<>();
        return List.of(infoImagePath.split(","));
    }

    private void clear(String destDir) {
        System.out.println("================ 파일 초기화 중 ==================");
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
