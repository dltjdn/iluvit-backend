package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final UserService userService;
    private final ImageService imageService;
    private final AuthNumberService authNumberService;
    private final ParentRepository parentRepository;
    private final AuthNumberRepository authNumberRepository;
    private final ScrapRepository scrapRepository;
    private final CenterRepository centerRepository;
    private final ChildRepository childRepository;
    private final BookmarkService bookmarkService;



    /**
     * 작성날짜: 2022/05/13 4:44 PM
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 정보 반환
     */
    public ParentDetailResponse findDetail(Long id) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        ParentDetailResponse response = new ParentDetailResponse(findParent);

        // 현재 등록한 프로필 이미지가 있으면 보여주기
        if (findParent.getHasProfileImg()) {
            String imagePath = imageService.getUserProfileDir();
            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        }

        return response;
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

        // 유저 닉네임 중복 검사
        if(!Objects.equals(findParent.getNickName(), request.getNickname())){
            parentRepository.findByNickName(request.getNickname())
                    .ifPresent(parent -> {
                        throw new UserException("이미 존재하는 닉네임입니다.");
                    });
        }

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authNumberService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findParent.updateDetailWithPhoneNum(request, theme);
            // 인증번호 테이블에서 지우기
            authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findParent.updateDetail(request, theme);
        }
        ParentDetailResponse response = new ParentDetailResponse(findParent);

        // 요청에 프로필 이미지 있으면 덮어씌우기
        if (!request.getProfileImg().isEmpty()) {
            String imagePath = imageService.getUserProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath + findParent.getId());
            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        }

        return response;
    }

    /**
     * 작성날짜: 2022/05/24 11:40 AM
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    public void signup(SignupParentRequest request) {

        String hashedPwd = userService.signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum());
        Parent parent = request.createParent(hashedPwd);

        // default 스크랩 생성
        Scrap scrap = Scrap.createScrap(parent, "default");

        parentRepository.save(parent);
        scrapRepository.save(scrap);

        // 사용이 끝난 인증번호를 테이블에서 지우기
        authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
    }

    /**
    *   작성날짜: 2022/07/01 5:09 PM
    *   작성자: 이승범
    *   작성내용: 시설 찜하기
    */
    public void savePrefer(Long userId, Long centerId) {
        Parent parent = parentRepository.findByIdWithPreferWithCenter(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 사용자입니다."));

        parent.getPrefers().stream()
                .filter(prefer -> Objects.equals(prefer.getCenter().getId(), centerId))
    }
}
