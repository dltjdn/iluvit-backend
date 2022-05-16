package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.TeacherDetailResponse;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherDetailResponse findDetail(Long id) {
        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);

        // 부모의 프로필 사진 담기
        InputStream imageStream = new FileInputStream(profileImgPath + id + ".png");
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        String encodedImage = Base64.encodeBase64String(imageByteArray);
        response.setProfileImg(encodedImage);

        return response;

    }


}
