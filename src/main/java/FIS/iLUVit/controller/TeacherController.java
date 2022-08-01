package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 프로필 조회
     */
    @GetMapping("/teacher/detail")
    public TeacherDetailResponse findTeacherDetail(@Login Long id) throws IOException {
        return teacherService.findDetail(id);
    }

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 프로필 정보 update
     */
    @PostMapping("/teacher/detail")
    public TeacherDetailResponse updateTeacherDetail(@Login Long id, @Valid @ModelAttribute UpdateTeacherDetailRequest request) throws IOException {
        return teacherService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/05/24 5:24 PM
     * 작성자: 이승범
     * 작성내용: 교사 회원가입
     */
    @PostMapping("/signup/teacher")
    public void signup(@RequestBody @Valid SignupTeacherRequest request) {
        teacherService.signup(request);
    }

    /**
     * 작성날짜: 2022/06/30 11:59 AM
     * 작성자: 이승범
     * 작성내용: 시설에 등록신청
     */
    @PatchMapping("/teacher/{centerId}/assign")
    public void assignCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        teacherService.assignCenter(userId, centerId);
    }

    /**
     * 작성날짜: 2022/06/30 11:41 AM
     * 작성자: 이승범
     * 작성내용: 시설 탈퇴하기
     */
    @PatchMapping("/teacher/center/escape")
    public void escapeCenter(@Login Long userId) {
        teacherService.escapeCenter(userId);
    }

    /**
     * 작성날짜: 2022/06/29 11:31 AM
     * 작성자: 이승범
     * 작성내용: 교사 관리 페이지
     */
    @GetMapping("/director/teacher/approval")
    public TeacherApprovalListResponse teacherApprovalList(@Login Long userId) {
        return teacherService.findTeacherApprovalList(userId);
    }

    /**
     * 작성날짜: 2022/06/29 11:32 AM
     * 작성자: 이승범
     * 작성내용: 교사 승인
     */
    @PatchMapping("/director/teacher/accept/{teacherId}")
    public void acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/06/29 5:13 PM
     * 작성자: 이승범
     * 작성내용: 교사 삭제/승인거절
     */
    @PatchMapping("/director/teacher/fire/{teacherId}")
    public void fireTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.fireTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/07/01 3:07 PM
     * 작성자: 이승범
     * 작성내용: 원장권한 부여
     */
    @PatchMapping("/director/teacher/mandate/{teacherId}")
    public void mandateTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.mandateTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/07/29 5:05 PM
     * 작성자: 이승범
     * 작성내용: 원장권한 박탈
     */
    @PatchMapping("/director/teacher/demote/{teacherId}")
    public void demoteTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.demoteTeacher(userId, teacherId);
    }
}
