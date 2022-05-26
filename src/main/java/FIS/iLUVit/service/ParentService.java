package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AuthNumberRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.controller.dto.ChildInfoDTO;
import FIS.iLUVit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final AuthNumberRepository authNumberRepository;
    private final ImageService imageService;
    private final BCryptPasswordEncoder encoder;

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    public ChildInfoDTO ChildrenInfo(Long id) {
        Parent findParent = parentRepository.findWithChildren(id)
                .orElseThrow(() -> new UserException("존재하지 않는 User 입니다."));

        ChildInfoDTO childInfoDTO = new ChildInfoDTO();

        findParent.getChildren().forEach(child -> {
            childInfoDTO.getData().add(new ChildInfoDTO.ChildInfo(child));
        });

        return childInfoDTO;
    }

    /**
     * 작성날짜: 2022/05/16 11:42 AM
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 정보 업데이트
     */
    public ParentDetailResponse updateDetail(Long id, ParentDetailRequest request) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        // 관심사를 스트링에서 객체로 바꾸기
        ObjectMapper objectMapper = new ObjectMapper();
        Theme theme = objectMapper.readValue(request.getTheme(), Theme.class);

        try {
            parentRepository.findByNickName(request.getNickname()).orElseThrow(IllegalArgumentException::new);
            throw new UserException("이미 존재하는 닉네임 입니다.");
        } catch (IllegalArgumentException e) {
            findParent.updateDetail(request, theme);
        }

        String imagePath = imageService.getUserProfileDir();
        imageService.saveProfileImage(request.getProfileImg(), imagePath + findParent.getId());

        ParentDetailResponse response = new ParentDetailResponse(findParent);
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        return response;
    }

    /**
     * 작성날짜: 2022/05/13 4:44 PM
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 정보 반환
     */
    public ParentDetailResponse findDetail(Long id) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        ParentDetailResponse response = new ParentDetailResponse(findParent);

        String imagePath = imageService.getUserProfileDir();
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));

        return response;
    }

    /**
     * 작성날짜: 2022/05/24 11:40 AM
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    public void signup(SignupParentRequest request) {

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

        String hashedPwd = encoder.encode(request.getPassword());
        Parent parent = request.createParent(hashedPwd);

        parentRepository.save(parent);

        authNumberRepository.deleteAllByPhoneNum(request.getPhoneNum());
    }
}
