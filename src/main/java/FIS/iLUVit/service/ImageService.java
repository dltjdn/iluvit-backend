package FIS.iLUVit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class ImageService {

    @Value("${file.centerDir}")
    private String centerDir;
    @Value("${file.userDir}")
    private String userDir;
    @Value("${file.presentationDir}")
    private String presentationDir;

    /**
     * Center Id를 넣으면 해당 Center 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getCenterDir(Long id){
        return centerDir + String.valueOf(id) + "/";
    }

    /**
     * User Id를 넣으면 해당 Center 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getUserDir(Long id){
        return userDir + String.valueOf(id) + "/";
    }

    /**
     * presentation Id를 넣으면 해당 Center 의 이미지들이 있는 디렉토리 경로 반환
     */
    public String getPresentationDir(Long id){
        return presentationDir + String.valueOf(id) + "/";
    }

    /**
     * 해당 domain이 가지고 있는 디렉토리의 경로와 이미지 갯수를 인자값으로 주면 base64로 인코딩된 문자열 반환
     */
    public List<String> getEncodedImage(String imageDirPath, Integer cnt) throws IOException {
        List<String> images = new ArrayList<>();
        for(int i = 1; i <= cnt; i++){
            int finalI = i;
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return name.startsWith(String.valueOf(finalI));
                }
            };
            File file = new File(imageDirPath);
            File[] files = file.listFiles(filter);
            for (File temp : files){
                if(temp.isFile()){
                    String encodeImage = encodeImage(temp);
                    if(encodeImage != null){
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
    public String encodeImage(File file) throws IOException {
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
    public void saveImage(List<MultipartFile> images, String destDir) throws IOException {
        mkDir(destDir);
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
    }
}
