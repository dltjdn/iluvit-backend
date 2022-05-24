package FIS.iLUVit.service;

import FIS.iLUVit.exception.ImageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Component
public class ImageService {

    @Value("${file.centerDir}")
    private String centerInfoDir;
    @Value("${file.presentationDir}")
    private String presentationInfoDir;
    @Value("${file.postDir}")
    private String postInfoDir;

    @Value("${file.userProfileImagePath}")
    private String userProfileImagePath;
    @Value("${file.centerProfileImagePath}")
    private String centerProfileImagePath;
    @Value("${file.childProfileImagePath}")
    private String childProfileImagePath;




    /**
     * Center Id를 넣으면 해당 Center 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getCenterDir(Long id){
        return centerInfoDir + String.valueOf(id) + "/";
    }

    /**
     * presentation Id를 넣으면 해당 presentation 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getPresentationDir(Long id){
        return presentationInfoDir  + String.valueOf(id) + "/";
    }

    /**
     * post Id를 넣으면 해당 post 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getPostDir(Long id){
        return postInfoDir  + String.valueOf(id) + "/";
    }

    /**
     * User Id를 넣으면 해당 user profile 이미지 경로 반환
     */
    public String getUserProfileDir(){
        return userProfileImagePath;
    }


    /**
     * Center Id를 넣으면 해당 center profile 이미지 경로 반환
     */
    public String getCenterProfileDir(){
        return centerProfileImagePath;
    }

    /**
     * Presentation Id를 넣으면 해당 child profile 이미지 경로 반환
     */
    public String getChileProfileDir(){
        return childProfileImagePath;
    }

    /**
     * 해당 domain이 가지고 있는 디렉토리의 경로와 이미지 갯수를 인자값으로 주면 base64로 인코딩된 문자열 반환
     */
    public List<String> getEncodedInfoImage(String imageDirPath, Integer cnt) {
        List<String> images = new ArrayList<>();
        for(int i = 1; i <= cnt; i++){
            int finalI = i;
            FilenameFilter filter = (f, name) -> {
                String regex = "^" + String.valueOf(finalI) + "[.].+";
                return name.matches(regex);
            };
            File file = new File(imageDirPath);
            File[] files = file.listFiles(filter);
            if(files == null) throw new ImageException("해당 image가 없습니다");
            for (File temp : files){
                if(temp.isFile()){
                    String encodeImage = null;
                    try {
                        encodeImage = encodeImage(temp);
                    } catch (IOException e) {
                        log.error("{} 시설 이미지 로드중 {}에서 예외 발생", imageDirPath, temp.getName());
                        encodeImage = null;
                    }
                    if(encodeImage != null){
                        images.add(encodeImage);
                    }
                }
            }
        }
        return images;
    }

    /**
    *   작성날짜: 2022/05/17 11:12 AM
    *   작성자: 이승범
    *   작성내용: profileImg를 base64로 인고딩된 문자열 반환
    */
    public String getEncodedProfileImage(String imageDir, Long id) {
        FilenameFilter filter = (f, name) -> {
            String regex = "^" + id + "[.].+";
            return name.matches(regex);
        };
        File dir = new File(imageDir);
        File findFile;
        try {
            File[] files = Objects.requireNonNull(dir.listFiles(filter));
            if (files.length > 1) {
                log.error("pk로 되어있는 이미지가 {}개 있습니다.", files.length);
            }
            findFile = files[0];
            return encodeImage(findFile);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | IOException e) {
            return null;
        }
    }

    public Map<Long, String> getEncodedProfileImage(String imageDir, List<Long> idList){
        Map map = new HashMap<Long, String>();
        idList.forEach(id -> map.put(id, null));
        FilenameFilter filter = (f, name) -> {
            String regex = "^(";
            for(int i = 0; i < idList.size(); i++){
                if(i == idList.size() - 1){
                    regex += idList.get(i);
                }
                else regex = regex + idList.get(i) + '|';
            }
            regex += ")[.].+";
            return name.matches(regex);
        };
        File dir = new File(imageDir);
        File[] files = dir.listFiles(filter);
        for(File file : files){
            try {
                map.put(Long.valueOf(extractFileName(file.getName())), encodeImage(file));
            } catch (IOException e) {
                continue;
            }
        }
        return map;
    }

    /**
     * GetEncodedImage 메서드 에서 사용
     */
    private String encodeImage(File file) throws IOException {
        String encodedImage = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buf = IOUtils.toByteArray(inputStream);
            encodedImage = new String(Base64.getEncoder().encode(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    /**
     * multipart List로 받고 domain의 디렉터리 경로를 입력하면 파일 자동으로 저장
     */
    public void saveInfoImage(List<MultipartFile> images, String destDir) {
        mkDir(destDir);
        clear(destDir);
        int name = 1;
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                String originalFileName = image.getOriginalFilename();
                String extension = extractExt(originalFileName);
                try {
                    image.transferTo(new File(destDir + String.valueOf(name) + "." + extension));
                } catch (IOException e) {
                    log.error("{} 에 이미지 저장 오류 발생", destDir);
                }
                name++;
            }
        }
    }

    private void clear(String destDir) {
        File file = new File(destDir);
        File[] files = file.listFiles();
        if(files.length != 0){
            for(File temp : files){
                temp.delete();
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

    public void mkDir(String path){
        String[] names = path.split("/");
        String temp = "";
        for(String name : names){
            temp += name + "/";
            File folder = new File(temp);
            if(!folder.exists()){
                try {
                    folder.mkdir();
                } catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }
        new File(path + "video").mkdir();
    }

    public void saveProfileImage(MultipartFile image, String imagePath) throws IOException {
        if(!image.isEmpty()){
            String originalFileName = image.getOriginalFilename();
            String extension = extractExt(originalFileName);
            image.transferTo(new File(imagePath + "." + extension));
        }
    }

}
