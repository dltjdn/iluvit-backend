package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.SignupException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final ImageService imageService;
    private final SignService signService;
    private final UserService userService;
    private final CenterRepository centerRepository;
    private final TeacherRepository teacherRepository;
    private final AuthNumberRepository authNumberRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;

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
        if (findTeacher.getHasProfileImg()) {
            String imagePath = imageService.getUserProfileDir();
            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        }

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

        Optional<Teacher> byNickName = teacherRepository.findByNickName(request.getNickname());
        // 닉네임 중복 검사
        if (byNickName.isEmpty()) {
            // 핸드폰 번호도 변경하는 경우
            if (request.getChangePhoneNum()) {
                // 핸드폰 인증이 완료되었는지 검사
                signService.validateAuthNumber(request.getPhoneNum(), AuthKind.updatePhoneNum);
                // 핸드폰 번호와 함께 프로필 update
                findTeacher.updateDetailWithPhoneNum(request);
                // 인증번호 테이블에서 지우기
                authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.updatePhoneNum);
            } else { // 핸드폰 번호 변경은 변경하지 않는 경우
                findTeacher.updateDetail(request);
            }
        } else {
            throw new UserException("이미 존재하는 닉네임 입니다.");
        }

        TeacherDetailResponse response = new TeacherDetailResponse(findTeacher);

        // 프로필 이미지 수정
        if (!request.getProfileImg().isEmpty()) {
            String imagePath = imageService.getUserProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath + findTeacher.getId());
            response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        }

        return response;
    }

    /**
     * 작성날짜: 2022/06/15 1:03 PM
     * 작성자: 이승범
     * 작성내용: 교사 회원가입
     */
    public void signup(SignupTeacherRequest request) {

        // 회원가입 유효성 검사 및 비밀번호 해싱
        String hashedPwd = userService.signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum());

        // 교사 객체 생성
        // 센터를 선택한 경우
        if (request.getCenterId() != null) {
            Center center = centerRepository.findByIdWithTeacher(request.getCenterId())
                    .orElseThrow(() -> new SignupException("잘못된 시설로의 접근입니다."));
            Teacher teacher = request.createTeacher(center, hashedPwd);
            teacherRepository.save(teacher);
            // 시설에 대한 최초 승인요청일 경우 기본 게시판(자유, 공지, 정보, 영상)생성
            if (center.getTeachers().isEmpty()) {
                createCenterStory(center);
            }
            // 모두의 이야기 default boards bookmark 추가하기
            List<Board> defaultBoards = boardRepository.findDefaultByModu();
            for (Board defaultBoard : defaultBoards) {
                Bookmark bookmark = Bookmark.createBookmark(defaultBoard, teacher);
                bookmarkRepository.save(bookmark);
            }
        } else {   // 센터를 선택하지 않은 경우
            Teacher teacher = request.createTeacher(null, hashedPwd);
            teacherRepository.save(teacher);
        }

        authNumberRepository.deleteByPhoneNumAndAuthKind(request.getPhoneNum(), AuthKind.signup);
    }

    /**
    *   작성날짜: 2022/06/29 10:49 AM
    *   작성자: 이승범
    *   작성내용: 교사관리 페이지에 필요한 교사들 정보 조회
    */
    public TeacherApprovalListResponse findTeacherApprovalList(Long userId) {

        // 로그인한 사용자가 원장인지 확인 및 원장으로 등록되어있는 시설에 모든 교사들 갖오기
        Teacher director = teacherRepository.findDirectorByIdWithCenter(userId)
                .orElseThrow(() -> new UserException("해당 정보를 열람할 권한이 없습니다."));

        TeacherApprovalListResponse response = new TeacherApprovalListResponse();

        director.getCenter().getTeachers().forEach(teacher->{
            // 요청한 원장은 빼고 시설에 연관된 교사들 보여주기
            if(!Objects.equals(teacher.getId(), userId)){
                TeacherApprovalListResponse.TeacherInfoForAdmin teacherInfoForAdmin =
                        new TeacherApprovalListResponse.TeacherInfoForAdmin(teacher.getId(), teacher.getName(), teacher.getApproval(), teacher.getAuth());
                // 프로필 이미지 있는 교사들은 이미지 채우기
                if (teacher.getHasProfileImg()) {
                    String imagePath = imageService.getUserProfileDir();
                    String image = imageService.getEncodedProfileImage(imagePath, teacher.getId());
                    teacherInfoForAdmin.setProfileImg(image);
                }
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
        // 로그인한 사용자가 원장인지 확인
        Teacher director = teacherRepository.findDirectorByIdWithCenter(userId)
                .orElseThrow(() -> new UserException("해당 요청에대한 권한이 없습니다."));

        // 승인하고자 하는 교사가 해당 시설에 속해 있는지 && 대기 상태인지 확인
        Teacher acceptedTeacher = director.getCenter().getTeachers().stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId) && teacher.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 teacher_id 입니다."));

        teacherRepository.approveTeacher(teacherId, director.getCenter().getId());

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
    *   작성내용: 교사 해고
    */
    public void fireTeacher(Long userId, Long teacherId) {

        // 로그인한 사용자가 원장인지 확인 및 원장으로 등록되어있는 시설에 모든 교사들 갖오기
        Teacher director = teacherRepository.findDirectorByIdWithCenter(userId)
                .orElseThrow(() -> new UserException("해당 정보를 열람할 권한이 없습니다."));

        // 삭제하고자 하는 교사가 해당 시설에 소속되어 있는지 확인
        Teacher deletedTeacher = director.getCenter().getTeachers().stream()
                .filter(teacher -> Objects.equals(teacher.getId(), teacherId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 teacher_id 입니다."));

        teacherRepository.fireTeacher(teacherId);

        // 해당 시설과 연관된 bookmark 삭제
        if (deletedTeacher.getApproval() == Approval.ACCEPT) {
            List<Board> boards = boardRepository.findByCenter(director.getCenter().getId());
            List<Long> boardIds = boards.stream()
                    .map(Board::getId)
                    .collect(Collectors.toList());
            bookmarkRepository.deleteAllByBoardAndUser(deletedTeacher.getId(), boardIds);
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
