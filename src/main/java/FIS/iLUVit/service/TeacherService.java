package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final ImageService imageService;
    private final AuthService authService;
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }
    private final CenterRepository centerRepository;
    private final TeacherRepository teacherRepository;
    private final AuthRepository authRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final ScrapRepository scrapRepository;
    private final MapService mapService;

    private final AlarmRepository alarmRepository;

    /**
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 조회
     */
    public TeacherDetailResponse findTeacherDetails(Long id) throws IOException {

        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher,findTeacher.getProfileImagePath());

        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 update
     */
    public TeacherDetailResponse modifyTeacherInfo(Long id, TeacherDetailRequest request) throws IOException {

        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        // 유저 닉네임 중복 검사
        if (!Objects.equals(findTeacher.getNickName(), request.getNickname())) {
            teacherRepository.findByNickName(request.getNickname())
                    .ifPresent(teacher -> {
                        throw new SignupException(SignupErrorResult.DUPLICATED_NICKNAME);
                    });
        }

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findTeacher.updateDetailWithPhoneNum(request);
            // 인증번호 테이블에서 지우기
            authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findTeacher.updateDetail(request);
        }

        Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
        Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
        Location location = new Location(loAndLat, hangjung);
        findTeacher.updateLocation(location);

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher,findTeacher.getProfileImagePath());
        imageService.saveProfileImage(request.getProfileImg(), findTeacher);


        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 회원가입
     */
    public Teacher signupTeacher(SignupTeacherRequest request) {

        // 회원가입 유효성 검사 및 비밀번호 해싱
        String hashedPwd = userService.hashAndValidatePwdForSignup(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());

        // 교사 객체 생성
        Teacher teacher;
        // 센터를 선택한 경우
        if (request.getCenterId() != null) {
            Center center = centerRepository.findById(request.getCenterId())
                    .orElseThrow(() -> new SignupException(SignupErrorResult.NOT_EXIST_CENTER));
            teacher = request.createTeacher(center, hashedPwd);

            Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
            Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
            Location location = new Location(loAndLat, hangjung);
            teacher.updateLocation(location);
            imageService.saveProfileImage(null, teacher);
            teacherRepository.save(teacher);
            // 시설에 원장들에게 알람보내기
            List<Teacher> teacherList = teacherRepository.findByCenter(center);
            teacherList.forEach(t -> {
                if (t.getAuth() == Auth.DIRECTOR) {
                    Alarm alarm = new CenterApprovalReceivedAlarm(t, Auth.TEACHER, t.getCenter());
                    alarmRepository.save(alarm);
                    AlarmUtils.publishAlarmEvent(alarm);
                }
            });
        } else {   // 센터를 선택하지 않은 경우
            teacher = request.createTeacher(null, hashedPwd);
            Pair<Double, Double> loAndLat = mapService.convertAddressToLocation(request.getAddress());
            Pair<String, String> hangjung = mapService.getSidoSigunguByLocation(loAndLat.getFirst(), loAndLat.getSecond());
            Location location = new Location(loAndLat, hangjung);
            teacher.updateLocation(location);

            teacherRepository.save(teacher);
        }
        // 모두의 이야기 default boards bookmark 추가하기
        List<Board> defaultBoards = boardRepository.findByCenterIsNullAndIsDefaultTrue();
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, teacher);
            boardBookmarkRepository.save(bookmark);
        }

        // default 스크랩 생성
        Scrap scrap = Scrap.createDefaultScrap(teacher);
        scrapRepository.save(scrap);

        // 사용이 끝난 인증번호 지우기
        authRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);

        return teacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설에 등록신청
     */
    public Teacher requestAssignCenterForTeacher(Long userId, Long centerId) {
        Teacher teacher = teacherRepository.findByIdAndNotAssign(userId)
                .orElseThrow(() -> new SignupException(SignupErrorResult.ALREADY_BELONG_CENTER));

        // 시설과의 연관관계맺기
        Center center = centerRepository.getById(centerId);
        teacher.assignCenter(center);

        // 승인 요청 알람이 해당 시설의 원장들에게 감
        List<Teacher> directors = teacherRepository.findDirectorByCenter(centerId);
        directors.forEach(director -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(director, Auth.TEACHER, director.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);
        });
        return teacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설 탈퇴하기
     */
    public Teacher leaveCenterForTeacher(Long userId) {

        Teacher escapedTeacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new SignupException(SignupErrorResult.NOT_BELONG_CENTER));

        List<Teacher> teacherList = teacherRepository.findByCenter(escapedTeacher.getCenter());

        // 시설에 속한 일반 교사들
        List<Teacher> commons = teacherList.stream()
                .filter(teacher -> teacher.getAuth() == Auth.TEACHER)
                .collect(Collectors.toList());

        // 시설에 속한 원장들
        List<Teacher> directors = teacherList.stream()
                .filter(teacher -> teacher.getAuth() == Auth.DIRECTOR)
                .collect(Collectors.toList());

        // 일반 교사가 남아있을때 최후의 원장이 탈퇴하려면 남은 교사에게 원장 권한을 위임해야함
        if (escapedTeacher.getAuth() == Auth.DIRECTOR && directors.size() == 1 && !commons.isEmpty()) {
            throw new SignupException(SignupErrorResult.HAVE_TO_MANDATE);
        }

        // 속해있는 시설과 연관된 게시판 bookmark 모두 지우기
        deleteBookmarkByCenter(escapedTeacher);

        // 시설과의 연관관계 끊기
        escapedTeacher.exitCenter();
        return escapedTeacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사관리 페이지에 필요한 교사들 정보 조회
     */
    public List<TeacherInfoForAdminDto> findTeacherApprovalList(Long userId) {

        // 로그인한 사용자가 원장인지 확인
        Teacher director = teacherRepository.findDirectorById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        //원장으로 등록되어있는 시설에 모든 교사들 갖오기
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());

        List<TeacherInfoForAdminDto> response = new ArrayList<>();

        teacherList.forEach(teacher -> {
            // 요청한 원장은 빼고 시설에 연관된 교사들 보여주기
            if (!Objects.equals(teacher.getId(), userId)) {
                TeacherInfoForAdminDto teacherInfoForAdmin =
                        new TeacherInfoForAdminDto(teacher,teacher.getProfileImagePath());
                response.add(teacherInfoForAdmin);
            }
        });
        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 승인
     */
    public Teacher acceptTeacherRegistration(Long userId, Long teacherId) {
        // 로그인한 사용자가 원장인지 확인
        Teacher director = teacherRepository.findDirectorById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // 승인하고자 하는 교사가 해당 시설에 속해 있는지 && 대기 상태인지 확인
        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        Teacher acceptedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId) && teacher.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인
        acceptedTeacher.acceptTeacher();

        // center default boards bookmark 추가하기
        List<Board> defaultBoards = boardRepository.findByCenterAndIsDefaultTrue(director.getCenter());
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, acceptedTeacher);
            boardBookmarkRepository.save(bookmark);
        }

        return acceptedTeacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 삭제/승인거절
     */
    public Teacher rejectTeacherRegistration(Long userId, Long teacherId) {

        // 로그인한 사용자가 원장인지 확인
        Teacher director = teacherRepository.findDirectorById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        Teacher firedTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 삭제하고자 하는 교사가 해당 시설에 소속되어 있는지 확인
        if (firedTeacher.getCenter() == null || !Objects.equals(director.getCenter().getId(), firedTeacher.getCenter().getId())) {
            throw new UserException(UserErrorResult.NOT_VALID_REQUEST);
        }

        // 해당 시설과 연관된 게시판 bookmark 삭제
        deleteBookmarkByCenter(firedTeacher);

        // 시설과의 연관관계 끊기
        firedTeacher.exitCenter();
        return firedTeacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 원장권한 부여
     */
    public Teacher mandateTeacher(Long userId, Long teacherId) {
        // 로그인한 사용자가 원장인지 확인
        Teacher director = teacherRepository.findDirectorById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        Teacher mandatedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .filter(teacher -> teacher.getApproval() == Approval.ACCEPT)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        mandatedTeacher.beDirector();
        return mandatedTeacher;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 원장권한 박탈
     */
    public Teacher demoteTeacher(Long userId, Long teacherId) {

        Teacher director = teacherRepository.findDirectorById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        List<Teacher> teacherList = teacherRepository.findByCenter(director.getCenter());
        Teacher demotedTeacher = teacherList.stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        demotedTeacher.beTeacher();
        return demotedTeacher;
    }

    // 해당 시설과 연관된 게시판 bookmark 삭제
    private void deleteBookmarkByCenter(Teacher escapedTeacher) {
        if (escapedTeacher.getApproval() == Approval.ACCEPT) {
            List<Board> boards = boardRepository.findByCenter(escapedTeacher.getCenter());
            boardBookmarkRepository.deleteByUserAndBoardIn(escapedTeacher, boards);
        }
        // scrap 없애는 코드 추가
    }

    /**
     *    회원가입 과정에서 필요한 센터정보 가져오기
     */
    public Slice<CenterDto> findCenterForSignupTeacher(CenterRequest request, Pageable pageable) {
        List<Center> centers = centerRepository.findForSignup(request.getSido(), request.getSigungu(), request.getCenterName());

        List<CenterDto> centerDtos = centers.stream()
                .map(CenterDto::new) // Center를 CenterDto로 변환
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (centers.size() > pageable.getPageSize()) {
            hasNext = true;
            centers.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerDtos, pageable, hasNext);
    }

    /**
     *   작성자: 이서우
     *   작성내용: 교사 회원 탈퇴 ( 공통 제외 교사만 가지고 있는 탈퇴 플로우 )
     */
    public long withdrawTeacher(Long userId){
        userService.withdrawUser(userId); // 교사, 학부모 공톤 탈퇴 로직
        leaveCenterForTeacher(userId); // 연결된 시설 끊기 ( 해당 시설과 연관된 bookmark 삭제 )
        return userId;
    }

}
