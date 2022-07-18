package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.SignupErrorResult;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final ImageService imageService;
    private final AuthNumberService authNumberService;
    private final UserService userService;
    private final CenterRepository centerRepository;
    private final TeacherRepository teacherRepository;
    private final AuthNumberRepository authNumberRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ScrapRepository scrapRepository;

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 조회
     */
    public TeacherDetailResponse findDetail(Long id) throws IOException {

        Teacher findTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);

        // 현재 등록한 프로필 이미지가 있으면 보여주기
//        if (findTeacher.getHasProfileImg()) {
//            String imagePath = imageService.getUserProfileDir();
//            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
//        }
        response.setProfileImg(imageService.getProfileImage(findTeacher));
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

        // 유저 닉네임 중복 검사
        if(!Objects.equals(findTeacher.getNickName(), request.getNickname())){
            teacherRepository.findByNickName(request.getNickname())
                    .ifPresent(teacher -> {
                        throw new UserException("이미 중복된 닉네입니다.");
                    });
        }

        // 핸드폰 번호도 변경하는 경우
        if (request.getChangePhoneNum()) {
            // 핸드폰 인증이 완료되었는지 검사
            authNumberService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
            // 핸드폰 번호와 함께 프로필 update
            findTeacher.updateDetailWithPhoneNum(request);
            // 인증번호 테이블에서 지우기
            authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
        } else { // 핸드폰 번호 변경은 변경하지 않는 경우
            findTeacher.updateDetail(request);
        }

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);

        // 프로필 이미지 수정
//        if (!request.getProfileImg().isEmpty()) {
//            String imagePath = imageService.getUserProfileDir();
//            imageService.saveProfileImage(request.getProfileImg(), imagePath + findTeacher.getId());
//            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
//        }
        imageService.saveProfileImage(request.getProfileImg(), findTeacher);

        return response;
    }

    /**
     * 작성날짜: 2022/06/15 1:03 PM
     * 작성자: 이승범
     * 작성내용: 교사 회원가입
     */
    public void signup(SignupTeacherRequest request) {

        // 회원가입 유효성 검사 및 비밀번호 해싱
        String hashedPwd = userService.signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());

        // 교사 객체 생성
        Teacher teacher;
        // 센터를 선택한 경우
        if (request.getCenterId() != null) {
            Center center = centerRepository.findByIdWithTeacher(request.getCenterId())
                    .orElseThrow(() -> new SignupException(SignupErrorResult.NOT_EXIST_CENTER));
            teacher = request.createTeacher(center, hashedPwd);
            teacherRepository.save(teacher);
            // 시설에 원장들에게 알람보내기
            center.getTeachers().forEach(t->{
                if (t.getAuth() == Auth.DIRECTOR) {
                    AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(t));
                }
            });
            // 시설에 대한 최초 승인요청일 경우 기본 게시판(자유, 공지, 정보, 영상)생성
            if (center.getTeachers().isEmpty()) {
                createCenterStory(center);
            }
        } else {   // 센터를 선택하지 않은 경우
            teacher = request.createTeacher(null, hashedPwd);
            teacherRepository.save(teacher);
        }
        // 모두의 이야기 default boards bookmark 추가하기
        List<Board> defaultBoards = boardRepository.findDefaultByModu();
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, teacher);
            bookmarkRepository.save(bookmark);
        }

        // default 스크랩 생성
        Scrap scrap = Scrap.createScrap(teacher, "default");

        scrapRepository.save(scrap);

        authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
    }

    /**
     *   작성날짜: 2022/06/30 12:04 PM
     *   작성자: 이승범
     *   작성내용: 시설에 등록신청
     */
    public void assignCenter(Long userId, Long centerId) {
        teacherRepository.findByIdAndNotAssign(userId)
                .orElseThrow(()->new UserException("현재 속해있는 시설이 있습니다."));

        // centerId가 유효하지 않다면 예외처리
        try {
            teacherRepository.assignCenter(userId, centerId);

            // 승인 요청 알람이 해당 시설의 원장들에게 감
            List<Teacher> directors = teacherRepository.findDirectorByCenter(centerId);
            directors.forEach(director->{
                AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(director));
            });
        } catch (DataIntegrityViolationException e) {
            throw new UserException("존재하지 않는 시설입니다.");
        }
    }

    /**
     *   작성날짜: 2022/06/30 11:43 AM
     *   작성자: 이승범
     *   작성내용: 시설 스스로 탈주하기
     */
    public void escapeCenter(Long userId) {

        Teacher escapedTeacher = teacherRepository.findByIdWithCenterWithTeacher(userId)
                .orElseThrow(()-> new UserException("현재 속해있는 시설이 없습니다."));

        // 시설에 속한 일반 교사들
        List<Teacher> commons = escapedTeacher.getCenter().getTeachers().stream()
                .filter(teacher -> teacher.getAuth() == Auth.TEACHER)
                .collect(Collectors.toList());
        // 시설에 속한 원장들
        List<Teacher> directors = escapedTeacher.getCenter().getTeachers().stream()
                .filter(teacher -> teacher.getAuth() == Auth.DIRECTOR)
                .collect(Collectors.toList());

        // 일반 교사가 남아있을때 최후의 원장이 탈퇴하려면 남은 교사에게 원장 권한을 위임해야함
        if (directors.size() == 1 && !commons.isEmpty()) {
            throw new UserException("원장 권한을 다른 교사에게 넘겨주세요");
        }

        // 속해있는 시설과 연관된 bookmark 모두 지우기
        deleteBookmarkByCenter(escapedTeacher);

        // 시설과의 연관관계 끊기
        teacherRepository.exitCenter(userId);
    }

    /**
    *   작성날짜: 2022/06/29 10:49 AM
    *   작성자: 이승범
    *   작성내용: 교사관리 페이지에 필요한 교사들 정보 조회
    */
    public TeacherApprovalListResponse findTeacherApprovalList(Long userId) {

        // 로그인한 사용자가 원장인지 확인 및 원장으로 등록되어있는 시설에 모든 교사들 갖오기
        Teacher director = teacherRepository.findDirectorByIdWithCenterWithTeacher(userId)
                .orElseThrow(() -> new UserException("해당 정보를 열람할 권한이 없습니다."));

        TeacherApprovalListResponse response = new TeacherApprovalListResponse();

        director.getCenter().getTeachers().forEach(teacher->{
            // 요청한 원장은 빼고 시설에 연관된 교사들 보여주기
            if(!Objects.equals(teacher.getId(), userId)){
                TeacherApprovalListResponse.TeacherInfoForAdmin teacherInfoForAdmin =
                        new TeacherApprovalListResponse.TeacherInfoForAdmin(teacher.getId(), teacher.getName(), teacher.getApproval(), teacher.getAuth());
                // 프로필 이미지 있는 교사들은 이미지 채우기
//                if (teacher.getHasProfileImg()) {
//                    String imagePath = imageService.getUserProfileDir();
//                    String image = imageService.getEncodedProfileImage(imagePath, teacher.getId());
//                    teacherInfoForAdmin.setProfileImg(image);
//                }
                teacherInfoForAdmin.setProfileImg(imageService.getProfileImage(teacher));
                response.getData().add(teacherInfoForAdmin);
            }
        });
        return response;
    }

    /**
    *   작성날짜: 2022/06/29 11:31 AM
    *   작성자: 이승범
    *   작성내용: 교사 승인
    */
    public void acceptTeacher(Long userId, Long teacherId) {
        // 로그인한 사용자가 원장인지 확인 && 사용자 시설에 등록된 교사들 싹 다 가져오기
        Teacher director = teacherRepository.findDirectorByIdWithCenterWithTeacher(userId)
                .orElseThrow(() -> new UserException("해당 요청에대한 권한이 없습니다."));

        // 승인하고자 하는 교사가 해당 시설에 속해 있는지 && 대기 상태인지 확인
        Teacher acceptedTeacher = director.getCenter().getTeachers().stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId) && teacher.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 teacher_id 입니다."));

        teacherRepository.acceptTeacher(teacherId, director.getCenter().getId());



        // center default boards bookmark 추가하기
        List<Board> defaultBoards = boardRepository.findDefaultByCenter(director.getCenter().getId());
        for (Board defaultBoard : defaultBoards) {
            Bookmark bookmark = Bookmark.createBookmark(defaultBoard, acceptedTeacher);
            bookmarkRepository.save(bookmark);
        }
    }

    /**
    *   작성날짜: 2022/06/29 5:16 PM
    *   작성자: 이승범
    *   작성내용: 교사 삭제/승인거절
    */
    public void fireTeacher(Long userId, Long teacherId) {

        // 로그인한 사용자가 원장인지 확인 및 원장으로 등록되어있는 시설에 모든 교사들 갖오기
        Teacher director = teacherRepository.findDirectorByIdWithCenterWithTeacher(userId)
                .orElseThrow(() -> new UserException("해당 정보를 열람할 권한이 없습니다."));

        // 삭제하고자 하는 교사가 해당 시설에 소속되어 있는지 확인
        Teacher firedTeacher = director.getCenter().getTeachers().stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 teacher_id 입니다."));


        // 해당 시설과 연관된 bookmark 삭제
        deleteBookmarkByCenter(firedTeacher);
        teacherRepository.exitCenter(teacherId);
    }

    /**
    *   작성날짜: 2022/07/01 3:09 PM
    *   작성자: 이승범
    *   작성내용: 원장권한 위임
    */
    public void mandateTeacher(Long userId, Long teacherId) {

        Teacher director = teacherRepository.findDirectorByIdWithCenterWithTeacher(userId)
                .orElseThrow(() -> new UserException("올바르지 않은 접근입니다."));

        Teacher mandatedTeacher = director.getCenter().getTeachers().stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 teacherId 입니다."));

        mandatedTeacher.beDirector();
    }

    // 해당 시설과 연관된 bookmark 삭제
    private void deleteBookmarkByCenter(Teacher escapedTeacher) {
        if (escapedTeacher.getApproval() == Approval.ACCEPT) {
            List<Board> boards = boardRepository.findByCenter(escapedTeacher.getCenter().getId());
            List<Long> boardIds = boards.stream()
                    .map(Board::getId)
                    .collect(Collectors.toList());
            bookmarkRepository.deleteAllByBoardAndUser(escapedTeacher.getId(), boardIds);
        }
    }

    // 시설에 대한 최초 승인요청일 경우 기본 게시판(자유, 공지, 정보, 영상)생성 -> 나중에 관리자가 원장승인을 해줬을때로 바꿔야됨 and center signed to true
    private void createCenterStory(Center center) {
        Board noticeBoard = Board.createBoard("공지사항", BoardKind.NOTICE, center, true);
        Board freeBoard = Board.createBoard("자유 게시판", BoardKind.NORMAL, center, true);
        Board infoBoard = Board.createBoard("정보 게시판", BoardKind.NORMAL, center, true);
        Board videoBoard = Board.createBoard("영상 게시판", BoardKind.VIDEO, center, true);
        boardRepository.save(noticeBoard);
        boardRepository.save(freeBoard);
        boardRepository.save(infoBoard);
        boardRepository.save(videoBoard);
    }
}
