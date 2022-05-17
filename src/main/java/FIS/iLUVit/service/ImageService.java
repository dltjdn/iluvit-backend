package FIS.iLUVit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class ImageService {

    @Value("${file.centerDir}")
    private String centerInfoDir;
    @Value("${file.presentationDir}")
    private String presentationInfoDir;

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
        return presentationInfoDir + String.valueOf(id) + "/";
    }

    /**
     * User Id를 넣으면 해당 user profile 이미지 경로 반환
     */
    public String getUserProfileImagePath(Long id){
        return userProfileImagePath + String.valueOf(id);
    }


    /**
     * Center Id를 넣으면 해당 center profile 이미지 경로 반환
     */
    public String getCenterProfileImagePath(Long id){
        return centerProfileImagePath + String.valueOf(id);
    }

    /**
     * Presentation Id를 넣으면 해당 child profile 이미지 경로 반환
     */
    public String getChileProfileImagePath(Long id){
        return childProfileImagePath + String.valueOf(id);
    }

    /**
     * 해당 domain이 가지고 있는 디렉토리의 경로와 이미지 갯수를 인자값으로 주면 base64로 인코딩된 문자열 반환
     */
    public List<String> getEncodedInfoImage(String imageDirPath, Integer cnt) throws IOException {
        List<String> images = new ArrayList<>();
        for(int i = 1; i <= cnt; i++){
            int finalI = i;
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File f, String name) {
                    String regex = "^" + String.valueOf(finalI) + "[.].+";
                    return name.matches(regex);
                }
            };
            File file = new File(imageDirPath);
            File[] files = file.listFiles(filter);
            for (File temp : files){
                if(temp.isFile()){
                    String encodeImage = encodeImage(temp);
                    if(encodeImage != null){
                        System.out.println("temp = " + temp.getName());
                        images.add(encodeImage);
                    }
                }
            }
        }
        return images;
    }

    /**
     * GetEncodedImage 메서드 에서 사용
     */
    private String encodeImage(File file) throws IOException {
        String encodedImage = null;
        FileInputStream inputStream =  null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }
            byte[] fileArray = byteArrayOutputStream.toByteArray();
            encodedImage = new String(Base64.getEncoder().encode(fileArray));
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            inputStream.close();
            byteArrayOutputStream.close();
        }
        return encodedImage;
    }

    /**
     * multipart List로 받고 domain의 디렉터리 경로를 입력하면 파일 자동으로 저장
     */
    public void saveInfoImage(List<MultipartFile> images, String destDir) throws IOException {
        mkDir(destDir);
        clear(destDir);
        int name = 1;
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                String originalFileName = image.getOriginalFilename();
                String extension = extractExt(originalFileName);
                image.transferTo(new File(destDir + String.valueOf(name) + "." + extension));
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

    private void mkProfileDir(){
        mkDir(userProfileImagePath);
        mkDir(centerProfileImagePath);
        mkDir(childProfileImagePath);
    }

    public void saveProfileImage(MultipartFile image, String imagePath) throws IOException {
        mkProfileDir();
        if(!image.isEmpty()){
            String originalFileName = image.getOriginalFilename();
            String extension = extractExt(originalFileName);
            image.transferTo(new File(imagePath + "." + extension));
        }
    }

}
