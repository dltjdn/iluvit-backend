package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.TeacherRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final ImageService imageService;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final AuthNumberRepository authNumberRepository;
    private final BCryptPasswordEncoder encoder;


    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 조회
     */
    public TeacherDetailResponse findDetail(Long id) throws IOException {

        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);

        String imagePath = imageService.getUserProfileDir();
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));

        return response;
    }


    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 update
     */
    public TeacherDetailResponse updateDetail(Long id, UpdateTeacherDetailRequest request) throws IOException {

        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로 사용자 접근입니디."));

        try {
            teacherRepository.findByNickName(request.getNickname()).orElseThrow(IllegalArgumentException::new);
            throw new UserException("이미 존재하는 닉네임 입니다.");
        } catch (IllegalArgumentException e) {
            findTeacher.updateDetail(request);
        }

        String imagePath = imageService.getUserProfileDir();
        imageService.saveProfileImage(request.getProfileImg(), imagePath + findTeacher.getId());

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));

        return response;
    }

    /**
    *   작성날짜: 2022/06/15 1:03 PM
    *   작성자: 이승범
    *   작성내용: 교사 회원가입
    */
    public void signup(SignupTeacherRequest request) {

        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new SignupException("비밀번호와 비밀번호확인이 서로 다릅니다.");
        }

        User reduplicatedUser = userRepository.findByLoginId(request.getLoginId()).orElse(null);
        if (reduplicatedUser != null) {
            throw new SignupException("중복된 닉네임입니다.");
        }

        AuthNumber authComplete = authNumberRepository.findAuthComplete(request.getPhoneNum(), AuthKind.signup).orElse(null);
        if (authComplete == null) {
            throw new SignupException("핸드폰 인증이 완료되지 않았습니다.");
        } else if (Duration.between(authComplete.getAuthTime(), LocalDateTime.now()).getSeconds() > (60 * 60)) {
            throw new SignupException("핸드폰 인증시간이 만료되었습니다. 핸드폰 인증을 다시 해주세요");
        }

        Center center = centerRepository.findByIdAndSigned(request.getCenterId(), true)
                .orElseThrow(() -> new SignupException("원장으로 등록된 계정이 이미 존재하는 시설입니다."));

        String hashedPwd = encoder.encode(request.getPassword());
        Teacher teacher = request.createTeacher(center, hashedPwd);

        teacherRepository.save(teacher);

        authNumberRepository.deleteAllByPhoneNum(request.getPhoneNum());
    }


}
