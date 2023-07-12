package FIS.iLUVit.service;

import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.dto.parent.ParentDetailRequest;
import FIS.iLUVit.dto.parent.ParentDetailResponse;
import FIS.iLUVit.dto.parent.SignupParentRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final UserService userService;
    private final ImageService imageService;
    private final AuthService authService;
    private final ParentRepository parentRepository;
    private final AuthRepository authRepository;
    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final MapService mapService;
    private final ChildRepository childRepository;
    private final ChildService childService;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final CenterBookmarkService centerBookmarkService;
    private final ParticipationRepository participationRepository;
    private final WaitingRepository waitingRepository;
    private final WaitingService waitingService;



    /**
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 정보 반환
     */
    public ParentDetailResponse findParentDetails(Long id) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        ParentDetailResponse response = new ParentDetailResponse(findParent,imageService.getProfileImage(findParent));
        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 정보 수정
     */
    public ParentDetailResponse modifyParentInfo(Long id, ParentDetailRequest request) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        // 관심사를 스트링에서 객체로 바꾸기
        ObjectMapper objectMapper = new ObjectMapper();
        Theme theme = objectMapper.readValue(request.getTheme(), Theme.class);

        // 유저 닉네임 중복 검사
        if(!Objects.equals(findParent.getNickName(), request.getNickname())){
            parentRepository.findByNickName(request.getNickname())
                    .ifPresent(parent -> {
                        throw new UserException(UserErrorResult.ALREADY_NICKNAME_EXIST);
                    });
        }

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findParent.updateDetailWithPhoneNum(request, theme);
            // 인증번호 테이블에서 지우기
            authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findParent.updateDetail(request, theme);
        }

        Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
        Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
        Location location = new Location(loAndLat, hangjung);
        findParent.updateLocation(location);

        ParentDetailResponse response = new ParentDetailResponse(findParent,imageService.getProfileImage(findParent));
        imageService.saveProfileImage(request.getProfileImg(), findParent);
        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    public Parent signupParent(SignupParentRequest request) {

        String hashedPwd = userService.hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
        Parent parent = request.createParent(hashedPwd);

        Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
        Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
        Location location = new Location(loAndLat, hangjung);
        parent.updateLocation(location);

        // default 스크랩 생성
        Scrap scrap = Scrap.createDefaultScrap(parent);

        imageService.saveProfileImage(null, parent);

        parentRepository.save(parent);
        scrapRepository.save(scrap);

        // 사용이 끝난 인증번호를 테이블에서 지우기
        authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);

        // 모두의 이야기 default boards bookmark 추가하기
        List<Board> defaultBoards = boardRepository.findByCenterIsNullAndIsDefaultTrue();
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, parent);
            boardBookmarkRepository.save(bookmark);
        }
        return parent;
    }

    /**
     *   작성자: 이서우
     *   작성내용: 학부모 회원 탈퇴 ( 공통 제외 학부모만 가지고 있는 탈퇴 플로우)
     */
    public long withdrawParent(Long userId){
        userService.withdrawUser(userId);  // 교사, 학부모 공톤 탈퇴 로직

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        // 찜한 시설 리스트 삭제
        centerBookmarkRepository.findByParent(parent).forEach(centerBookmark -> {
            centerBookmarkService.deleteCenterBookmark(userId, centerBookmark.getCenter().getId());
        });


        // 아이 삭제 & 아이가 연관된 유치원 연관관계 끊기(해당 시설과 관련된 bookmark 모두 삭제)
        childRepository.findByParent(parent).forEach(child -> {
            childService.deleteChild(userId, child.getId());
        });


        // 신청되어있는 설명회 신청 목록에서 빠지게 하기 ( 설명회 신청 삭제 )
        participationRepository.findByParent(parent).forEach(participation -> {
            participationRepository.deleteById(participation.getId());
        });

        // 신청되어있는 설명회 대기 목록에서 빠지게 하기 ( 설명회 대기 취소 )
        waitingRepository.findByParent(parent).forEach(waiting-> {
            waitingService.cancelParticipation(waiting.getId(), userId);
        });

        return userId;
    }

}
