package FIS.iLUVit.domain.parent.service;

import FIS.iLUVit.domain.authnum.repository.AuthRepository;
import FIS.iLUVit.domain.authnum.service.AuthService;
import FIS.iLUVit.domain.blackuser.service.BlackUserService;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.domain.Bookmark;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.boardbookmark.service.BoardBookmarkService;
import FIS.iLUVit.domain.centerbookmark.repository.CenterBookmarkRepository;
import FIS.iLUVit.domain.centerbookmark.service.CenterBookmarkService;
import FIS.iLUVit.domain.child.repository.ChildRepository;
import FIS.iLUVit.domain.child.service.ChildService;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.participation.repository.ParticipationRepository;
import FIS.iLUVit.domain.participation.service.ParticipationService;
import FIS.iLUVit.domain.scrap.domain.Scrap;
import FIS.iLUVit.domain.scrap.repository.ScrapRepository;
import FIS.iLUVit.domain.scrap.service.ScrapService;
import FIS.iLUVit.domain.waiting.repository.WaitingRepository;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.parent.dto.ParentUpdateRequest;
import FIS.iLUVit.domain.parent.dto.ParentFindOneResponse;
import FIS.iLUVit.domain.parent.dto.ParentCreateRequest;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.authnum.domain.AuthKind;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.common.service.ImageService;
import FIS.iLUVit.domain.common.service.MapService;
import FIS.iLUVit.domain.user.service.UserService;
import FIS.iLUVit.domain.waiting.service.WaitingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ParentRepository parentRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final AuthService authService;
    private final AuthRepository authRepository;
    private final ParticipationService participationService;
    private final MapService mapService;
    private final ChildService childService;
    private final CenterBookmarkService centerBookmarkService;
    private final WaitingService waitingService;
    private final BlackUserService blackUserService;
    private final BoardBookmarkService boardBookmarkService;
    private final ScrapService scrapService;

    /**
     *  학부모 정보 상세 조회
     */
    public ParentFindOneResponse findParentDetails(Long userId) {

        Parent parent = getParent(userId);

        return ParentFindOneResponse.from(parent);
    }

    /**
     *  학부모 정보 수정
     */
    public ParentFindOneResponse modifyParentInfo(Long userId, ParentUpdateRequest request) throws IOException {
        Parent findParent = getParent(userId);

        // 유저 닉네임 중복 검사
        if(!Objects.equals(findParent.getNickName(), request.getNickname())){
            parentRepository.findByNickName(request.getNickname())
                    .ifPresent(parent -> {
                        throw new UserException(UserErrorResult.DUPLICATE_NICKNAME);
                    });
        }

        // 관심사를 스트링에서 객체로 바꾸기
        ObjectMapper objectMapper = new ObjectMapper();
        Theme theme = objectMapper.readValue(request.getTheme(), Theme.class);

        Location location = mapService.getLocationInfo(request.getAddress());

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findParent.updateParentInfoWithPhoneNum(request, theme, location);
            // 인증번호 테이블에서 지우기
            authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findParent.updateParentInfo(request, theme, location);
        }
        imageService.saveProfileImage(request.getProfileImg(), findParent);

        return ParentFindOneResponse.from(findParent);
    }

    /**
     * 학부모 생성 (학부모 회원가입)
     */
    public void signupParent(ParentCreateRequest request) {
        // 블랙 유저 검증
        blackUserService.isValidUser(request.getPhoneNum());

        String hashedPwd = userService.hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
        Location location = mapService.getLocationInfo(request.getAddress());

        Parent parent = Parent.of(request,hashedPwd,location);
        parentRepository.save(parent);

        // default 스크랩 생성
        scrapService.saveDefaultSrap(parent);

        imageService.saveProfileImage(null, parent);

        // 사용이 끝난 인증번호를 테이블에서 지우기
        authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);

        // 모두의 이야기 default boards bookmark 추가하기
        boardBookmarkService.saveDefaultBoardBookmark(null, parent);

    }

    /**
     * 학부모 회원 탈퇴  ( 공통 제외 학부모만 가지고 있는 탈퇴 플로우)
     */
    public void withdrawParent(Long userId){
        userService.withdrawUser(userId);  // 교사, 학부모 공톤 탈퇴 로직

        Parent parent = getParent(userId);

        // 찜한 시설 리스트 삭제
        centerBookmarkService.deleteCenterBookmarkByWithdraw(userId, parent);

        // 아이 삭제 & 아이가 연관된 유치원 연관관계 끊기(해당 시설과 관련된 bookmark 모두 삭제)
        childService.deleteChildByWithdraw(userId, parent);

        // 신청되어있는 설명회 신청 목록에서 빠지게 하기 ( 설명회 신청 삭제 )
        participationService.deleteParticipationByWithdraw(parent);

        // 신청되어있는 설명회 대기 목록에서 빠지게 하기 ( 설명회 대기 취소 )
        waitingService.deleteWaitingByWithdraw(userId, parent);
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return  parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

}
