package FIS.iLUVit.domain.teacher.service;

import FIS.iLUVit.domain.alarm.service.AlarmService;
import FIS.iLUVit.domain.authnum.domain.AuthKind;
import FIS.iLUVit.domain.authnum.repository.AuthRepository;
import FIS.iLUVit.domain.authnum.service.AuthService;
import FIS.iLUVit.domain.blackuser.service.BlackUserService;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.boardbookmark.service.BoardBookmarkService;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.dto.CenterFindForUserRequest;
import FIS.iLUVit.domain.center.dto.CenterFindForUserResponse;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.common.service.ImageService;
import FIS.iLUVit.domain.common.service.MapService;
import FIS.iLUVit.domain.scrap.domain.Scrap;
import FIS.iLUVit.domain.scrap.repository.ScrapRepository;
import FIS.iLUVit.domain.scrap.service.ScrapService;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.dto.TeacherFindOneResponse;
import FIS.iLUVit.domain.teacher.dto.TeacherInfoForAdminResponse;
import FIS.iLUVit.domain.teacher.dto.TeacherSignupRequest;
import FIS.iLUVit.domain.teacher.dto.TeacherUpdateRequest;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.common.domain.Auth.DIRECTOR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {
    private final ImageService imageService;
    private final AuthService authService;
    private final AlarmService alarmService;
    private final CenterRepository centerRepository;
    private final TeacherRepository teacherRepository;
    private final AuthRepository authRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapService scrapService;
    private final MapService mapService;
    private final BlackUserService blackUserService;

    private final BoardBookmarkService boardBookmarkService;

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    /**
     * 교사의 상세 정보를 조회합니다
     */
    public TeacherFindOneResponse findTeacherDetails(Long userId) {
        Teacher teacher = getTeacher(userId);

        return TeacherFindOneResponse.from(teacher);
    }


    /**
     * 교사 회원가입을 수행합니다
     */
    public void signupTeacher(TeacherSignupRequest request) {
        // 블랙 유저 검증
        blackUserService.isValidUser(request.getPhoneNum());

        // 회원가입 유효성 검사 및 비밀번호 해싱
        String hashedPwd = userService.hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());

        // 시설 객체 초기화
        Center center = centerRepository.findById(request.getCenterId())
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));

        // 주소를 좌표로 변환하고 시도와 시군구 정보 가져오기
        Location location = mapService.getLocationInfo(request.getAddress());

        // 교사 객체를 생성하고 시설과 연결
        Teacher teacher = Teacher.of(request, hashedPwd, center, location);

        // 프로필 이미지 저장
        imageService.saveProfileImage(null, teacher);
        teacherRepository.save(teacher);

        // 모두의 이야기 기본 게시판들을 게시판 즐겨찾기에 추가
        boardBookmarkService.saveDefaultBoardBookmark(null, teacher);

        // 기본 스크랩 폴더 생성
        scrapService.saveDefaultSrap(teacher);

        // 사용이 끝난 인증번호 삭제
        authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);

        // 시설의 관리교사에게 알림 보내기
        List<Teacher> directors = teacherRepository.findByCenterAndAuth(center, DIRECTOR);
        alarmService.sendCenterApprovalReceivedAlarm(directors);

    }


    /**
     * 교사 정보를 변경합니다
     */
    public void updateTeacherInfo(Long userId, TeacherUpdateRequest request) {
        Teacher findTeacher = getTeacher(userId);

        // 유저 닉네임 중복 검사
        if (!Objects.equals(findTeacher.getNickName(), request.getNickname())) {
            teacherRepository.findByNickName(request.getNickname())
                    .ifPresent(teacher -> {
                        throw new UserException(UserErrorResult.DUPLICATE_NICKNAME);
                    });
        }

        // 주소를 좌표로 변환하고, 시도와 시군구 정보 가져오기
        Location location = mapService.getLocationInfo(request.getAddress());

        if (request.getChangePhoneNum()) { // 핸드폰 번호도 변경하는 경우

            authService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum); // 핸드폰 인증이 완료되었는지 검사

            findTeacher.updateTeacherInfoWithPhoneNum(request, location);  // 핸드폰 번호와 함께 프로필 update

            authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum); // 인증번호 테이블에서 해당 번호의 인증 정보 삭제
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findTeacher.updateTeacherInfo(request, location);
        }

        // 프로필 이미지 저장
        imageService.saveProfileImage(request.getProfileImg(), findTeacher);
    }

    /**
     * 시설 정보를 조회합니다
     */
    public Slice<CenterFindForUserResponse> findCenterForSignupTeacher(CenterFindForUserRequest request, Pageable pageable) {
        List<Center> centers = centerRepository.findForSignup(request.getSido(), request.getSigungu(), request.getCenterName());

        List<CenterFindForUserResponse> responses = centers.stream()
                .map(CenterFindForUserResponse::from)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (centers.size() > pageable.getPageSize()) {
            hasNext = true;
            centers.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(responses, pageable, hasNext);
    }
 
    /**
     * 교사가 시설에 시설 등록 승인을 요청합니다
     */
    public void requestAssignCenterForTeacher(Long userId, Long centerId) {
        // 교사 조회 및 시설 할당 여부 확인
        Teacher teacher = getTeacher(userId);

        Center center = centerRepository.findById(centerId)
                        .orElseThrow(()-> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));

        teacher.assignCenter(center); // 시설과의 연관관계 맺기

        // 승인 요청 알람이 해당 시설의 관리교사에게 전송
        List<Teacher> directors = teacherRepository.findByCenterAndAuth(center, Auth.DIRECTOR);
        alarmService.sendCenterApprovalReceivedAlarm(directors);
    }

    /**
     * 특정 교사가 소속 시설을 탈퇴하는 기능을 수행합니다
     */
    public void leaveCenterForTeacher(Long userId) {
        // user id로 교사 정보 조회 및 소속 시설 여부 확인
        Teacher escapedTeacher = getTeacher(userId);

        // 교사와 연결된 시설의 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(escapedTeacher.getCenter());

        // 시설에 속한 일반교사 필터링
        List<Teacher> commons = teacherList.stream()
                .filter(teacher -> teacher.getAuth() == Auth.TEACHER)
                .collect(Collectors.toList());

        // 시설에 속한 관리교사 필터링
        List<Teacher> directors = teacherList.stream()
                .filter(teacher -> teacher.getAuth() == DIRECTOR)
                .collect(Collectors.toList());

        // 일반 교사가 남아있을때 최후의 관리교사이 탈퇴하려면 남은 교사에게 관리교사 권한을 위임해야함
        if (escapedTeacher.getAuth() == DIRECTOR && directors.size() == 1 && !commons.isEmpty()) {
            throw new UserException(UserErrorResult.HAVE_TO_MANDATE);
        }

        // 속해있는 시설과 연관된 게시판 즐겨찾기, 스크랩 모두 삭제
        boardBookmarkService.deleteBoardBookmarkByCenter(escapedTeacher);
        scrapService.deleteScrapByCenter(escapedTeacher);

        // 시설과의 연관관계 끊기
        escapedTeacher.exitCenter();
    }

    /**
     * 관리교사가 시설에 등록된 교사 리스트를 조회합니다
     */
    public List<TeacherInfoForAdminResponse> findTeacherApprovalList(Long userId) {
        // 관리교사인지 확인
        Teacher director = getDirectorByAuth(userId);

        // 관리교사로 등록 되어있는 시설의 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());

        List<TeacherInfoForAdminResponse> responses = teacherList.stream()
                .filter(teacher -> teacher.getId() != userId) // 요청한 관리교사는 제외하고 시설에 연관된 교사들 보여주기
                .map(TeacherInfoForAdminResponse::from)
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * 시설에 등록을 요청한 교사의 등록 요청을 승인합니다
     */
    public void acceptTeacherRegistration(Long userId, Long teacherId) {
        // 관리교사인지 확인
        Teacher director = getDirectorByAuth(userId);

        // 승인하고자 하는 교사가 해당 시설에 속해 있는지 && 대기 상태인지 확인
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        Teacher acceptedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId) && teacher.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 교사 등록 승인
        acceptedTeacher.acceptTeacher();

        // 해당 시설의 기본 게시판들을 교사의 게시판 즐겨찾기에 추가
        boardBookmarkService.saveDefaultBoardBookmark(director.getCenter(), acceptedTeacher);
    }


    /**
     * 시설에 등록을 요청한 교사의 등록 요청을 거절합니다 / 교사를 삭제합니다
     */
    public void rejectTeacherRegistration(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = getDirectorByAuth(userId);

        // 승인 요청 거절/삭제 하고자 하는 교사 정보 조회
        Teacher firedTeacher = getTeacher(teacherId);

        // 삭제하고자 하는 교사가 해당 시설에 소속되어 있는지 확인
        if (firedTeacher.getCenter() == null || !Objects.equals(director.getCenter().getId(), firedTeacher.getCenter().getId())) {
            throw new UserException(UserErrorResult.FORBIDDEN_ACCESS);
        }

        // 해당 시설과 연관된 게시판 즐겨찾기, 스크랩 삭제
        boardBookmarkService.deleteBoardBookmarkByCenter(firedTeacher);
        scrapService.deleteScrapByCenter(firedTeacher);

        // 시설과의 연관관계 끊기
        firedTeacher.exitCenter();
    }

    /**
     * 관리교사 권한을 부여합니다
     */
    public void mandateTeacher(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = getDirectorByAuth(userId);

        // 해당 시설에 소속된 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        // 주어진 teacherId를 가진 교사 중 승인된 교사 찾기
        Teacher mandatedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .filter(teacher -> teacher.getApproval() == Approval.ACCEPT)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 교사에게 관리교사 권한 부여
        mandatedTeacher.beDirector();
    }

    /**
     * 관리교사 권한을 박탈합니다
     */
    public void demoteTeacher(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = getDirectorByAuth(userId);

        // 해당 시설에 소속된 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        // 주어진 teacherId를 가진 교사 찾기
        Teacher demotedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 교사의 관리교사 권한 박탈
        demotedTeacher.beTeacher();
    }


    /**
     * 교사 회원 탈퇴를 수행합니다 ( 공통 제외 교사만 가지고 있는 탈퇴 플로우 )
     */
    public void withdrawTeacher(Long userId){
        // 교사, 학부모 공톤 탈퇴 로직
        userService.withdrawUser(userId);
        // 연결된 시설 끊기 ( 해당 시설과 연관된 bookmark 삭제 )
        leaveCenterForTeacher(userId);
    }

    /**
     * 예외처리 - 존재하는 해당 권한의 선생인가
     */
    private Teacher getDirectorByAuth(Long userId) {
        return teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 선생인가
     */
    private Teacher getTeacher(Long userId) {
        return teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

}
