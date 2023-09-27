package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.domain.enumtype.NotificationTitle;
import FIS.iLUVit.dto.center.CenterBasicResponse;
import FIS.iLUVit.dto.center.CenterBasicRequest;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminResponse;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.enumtype.Auth.DIRECTOR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final ImageService imageService;
    private final AuthService authService;
    private UserService userService;
    private final CenterRepository centerRepository;
    private final TeacherRepository teacherRepository;
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final ScrapRepository scrapRepository;
    private final MapService mapService;
    private final AlarmRepository alarmRepository;
    private final BlackUserService blackUserService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    /**
     * 교사의 상세 정보를 조회합니다
     */
    public TeacherDetailResponse findTeacherDetails(Long userId) throws IOException {
        // 유저 id로 교사 조회
        Teacher findTeacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 조회된 교사 정보와 프로필 이미지를 이용하여 TeacherDetailResponse 생성
        TeacherDetailResponse teacherDetailResponse = new TeacherDetailResponse(findTeacher,findTeacher.getProfileImagePath());

        return teacherDetailResponse;
    }

    /**
     * 교사 회원가입을 수행합니다
     */
    public void signupTeacher(SignupTeacherRequest request) {
        // 블랙 유저 검증
        blackUserService.isValidUser(request.getPhoneNum());

        // 회원가입 유효성 검사 및 비밀번호 해싱
        String hashedPwd = userService.hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());

        // 시설 객체 초기화
        Center center = null;
        // 교사가 회원가입 시 시설을 선택한 경우
        if (request.getCenterId() != null) {
            // 선택한 시설 정보 가져오기
            center = centerRepository.findById(request.getCenterId())
                    .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
        }
        // 교사 객체를 생성하고 시설과 연결 ( 시설을 선택하지 않았으면 center = null )
        Teacher teacher = request.createTeacher(center, hashedPwd);

        // 주소를 좌표로 변환하고 시도와 시군구 정보 가져오기
        Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
        Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
        Location location = new Location(loAndLat, hangjung);
        // 교사 위치 정보 업데이트
        teacher.updateLocation(location);
        // 프로필 이미지 저장
        imageService.saveProfileImage(null, teacher);

        // 교사 정보 저장
        teacherRepository.save(teacher);

        // 모두의 이야기 default boards bookmark 추가
        List<Board> defaultBoards = boardRepository.findByCenterIsNullAndIsDefaultTrue();
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, teacher);
            boardBookmarkRepository.save(bookmark);
        }
        // default 스크랩 생성
        Scrap scrap = Scrap.createDefaultScrap(teacher);
        scrapRepository.save(scrap);

        // 사용이 끝난 인증번호 삭제
        authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);

        if (teacher.getCenter() != null) { // 교사가 시설에 소속된 경우
            // 시설의 관리교사에게 알림 보내기
            List<Teacher> teacherList = teacherRepository.findByCenter(center);
            teacherList.forEach(t -> {
                if (t.getAuth() == DIRECTOR) {
                    Alarm alarm = new CenterApprovalReceivedAlarm(t, Auth.TEACHER, t.getCenter());
                    alarmRepository.save(alarm);
                    AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
                }
            });
        }
    }


    /**
     * 교사 정보를 변경합니다
     */
    public void modifyTeacherInfo(Long userId, TeacherDetailRequest request) throws IOException {
        // 유저 id로 교사 조회
        Teacher findTeacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 유저 닉네임 중복 검사
        if (!Objects.equals(findTeacher.getNickName(), request.getNickname())) {
            // 입력된 닉네임과 기존 교사 닉네임이 다를 경우에만 중복 검사 수행
            teacherRepository.findByNickName(request.getNickname())
                    .ifPresent(teacher -> {
                        throw new UserException(UserErrorResult.DUPLICATE_NICKNAME);
                    });
        }

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findTeacher.updateDetailWithPhoneNum(request);
            // 인증번호 테이블에서 해당 번호의 인증 정보 삭제
            authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findTeacher.updateDetail(request);
        }

        // 주소를 좌표로 변환하고, 시도와 시군구 정보 가져오기
        Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
        Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
        Location location = new Location(loAndLat, hangjung);

        // 교사의 위치 정보 업데이트
        findTeacher.updateLocation(location);
        // 프로필 이미지 저장
        imageService.saveProfileImage(request.getProfileImg(), findTeacher);
    }

    /**
     * 시설 정보를 조회합니다
     */
    public Slice<CenterBasicResponse> findCenterForSignupTeacher(CenterBasicRequest request, Pageable pageable) {
        List<Center> centers = centerRepository.findForSignup(request.getSido(), request.getSigungu(), request.getCenterName());

        List<CenterBasicResponse> centerBasicResponses = centers.stream()
                .map(CenterBasicResponse::new) // Center를 CenterDto로 변환
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (centers.size() > pageable.getPageSize()) {
            hasNext = true;
            centers.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerBasicResponses, pageable, hasNext);
    }
 
    /**
     * 교사가 시설에 시설 등록 승인을 요청합니다
     */
    public Teacher requestAssignCenterForTeacher(Long userId, Long centerId) {
        // 교사 조회 및 시설 할당 여부 확인
        Teacher teacher = teacherRepository.findByIdAndCenterIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 시설과의 연관관계 맺기
        Center center = centerRepository.getById(centerId);
        teacher.assignCenter(center);

        // 승인 요청 알람이 해당 시설의 관리교사에게 전송
        List<Teacher> directors = teacherRepository.findByCenterAndAuth(center, DIRECTOR);
        directors.forEach(director -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(director, Auth.TEACHER, director.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
        });

        return teacher;
    }

    /**
     * 특정 교사가 소속 시설을 탈퇴하는 기능을 수행합니다
     */
    public Teacher leaveCenterForTeacher(Long userId) {
        // user id로 교사 정보 조회 및 소속 시설 여부 확인
        Teacher escapedTeacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

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

        // 속해있는 시설과 연관된 게시판 bookmark 모두 삭제
        deleteBookmarkByCenter(escapedTeacher);

        // 시설과의 연관관계 끊기
        escapedTeacher.exitCenter();

        return escapedTeacher;
    }

    /**
     * 관리교사가 시설에 등록된 교사 리스트를 조회합니다
     */
    public List<TeacherInfoForAdminResponse> findTeacherApprovalList(Long userId) {
        // user id로 관리교사인지 확인
        Teacher director = teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 관리교사로 등록 되어있는 시설의 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());

        // 조회된 교사 정보를 저장할 리스트 초기화
        List<TeacherInfoForAdminResponse> response = new ArrayList<>();

        teacherList.forEach(teacher -> {
            // 요청한 관리교사는 제외하고 시설에 연관된 교사들 보여주기
            if (!Objects.equals(teacher.getId(), userId)) {
                // 각 교사에 대한 정보를 포함한 Dto 객체 생성
                TeacherInfoForAdminResponse teacherInfoForAdmin =
                        new TeacherInfoForAdminResponse(teacher,teacher.getProfileImagePath());
                response.add(teacherInfoForAdmin);
            }
        });

        return response;
    }

    /**
     * 시설에 등록을 요청한 교사의 등록 요청을 승인합니다
     */
    public Teacher acceptTeacherRegistration(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 승인하고자 하는 교사가 해당 시설에 속해 있는지 && 대기 상태인지 확인
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        Teacher acceptedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId) && teacher.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 교사 등록 승인
        acceptedTeacher.acceptTeacher();

        // 해당 시설의 기본 게시판들을 교사의 게시판 즐겨찾기에 추가
        List<Board> defaultBoards = boardRepository.findByCenterAndIsDefaultTrue(director.getCenter());
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, acceptedTeacher);
            boardBookmarkRepository.save(bookmark);
        }

        return acceptedTeacher;
    }

    /**
     * 시설에 등록을 요청한 교사의 등록 요청을 거절합니다 / 교사를 삭제합니다
     */
    public Teacher rejectTeacherRegistration(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 승인 요청 거절/삭제 하고자 하는 교사 정보 조회
        Teacher firedTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 삭제하고자 하는 교사가 해당 시설에 소속되어 있는지 확인
        if (firedTeacher.getCenter() == null || !Objects.equals(director.getCenter().getId(), firedTeacher.getCenter().getId())) {
            throw new UserException(UserErrorResult.FORBIDDEN_ACCESS);
        }

        // 해당 시설과 연관된 게시판 즐겨찾기 삭제
        deleteBookmarkByCenter(firedTeacher);

        // 시설과의 연관관계 끊기
        firedTeacher.exitCenter();

        return firedTeacher;
    }

    /**
     * 관리교사 권한을 부여합니다
     */
    public Teacher mandateTeacher(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

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

        return mandatedTeacher;
    }

    /**
     * 관리교사 권한을 박탈합니다
     */
    public Teacher demoteTeacher(Long userId, Long teacherId) {
        // user id로 관리교사인지 확인
        Teacher director = teacherRepository.findByIdAndAuth(userId, DIRECTOR)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 해당 시설에 소속된 교사 리스트 조회
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        // 주어진 teacherId를 가진 교사 찾기
        Teacher demotedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 교사의 관리교사 권한 박탈
        demotedTeacher.beTeacher();

        return demotedTeacher;
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
     * 해당 시설과 연관된 게시판의 게시판 즐겨찾기를 삭제합니다
     */
    private void deleteBookmarkByCenter(Teacher escapedTeacher) {
        // 교사의 승인 상태가 ACCEPT인지 확인
        if (escapedTeacher.getApproval() == Approval.ACCEPT) {
            // 교사의 소석 시설과 관련된 게시판 조회
            List<Board> boards = boardRepository.findByCenter(escapedTeacher.getCenter());
            // 교사와 관련된 게시판 즐겨찾기 삭제
            boardBookmarkRepository.deleteByUserAndBoardIn(escapedTeacher, boards);
        }
        // TODO scrap 없애는 코드 추가
    }

}
