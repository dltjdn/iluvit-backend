package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지 조회
     */
    @GetMapping("/teacher/detail")
    public TeacherDetailResponse findTeacherDetail(@Login Long id) throws IOException {
        return teacherService.findDetail(id);
    }

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 선생의 마이페이지에 정보 update
     */
    @PutMapping("/teacher/detail")
    public TeacherDetailResponse updateTeacherDetail(@Login Long id, @ModelAttribute UpdateTeacherDetailRequest request) throws IOException {
        return teacherService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/05/24 5:24 PM
     * 작성자: 이승범
     * 작성내용: 원장 회원가입
     */
    @PostMapping("/signup/teacher")
    public void signup(@RequestBody SignupTeacherRequest request) {
        teacherService.signup(request);
    }

    /**
    *   작성날짜: 2022/06/29 11:31 AM
    *   작성자: 이승범
    *   작성내용: 교사 관리 페이지 정보
    */
    @GetMapping("/teacher/approval")
    public TeacherApprovalListResponse teacherApprovalList(@Login Long userId) {
        return teacherService.findTeacherApprovalList(userId);
    }

    /**
    *   작성날짜: 2022/06/29 11:32 AM
    *   작성자: 이승범
    *   작성내용: 교사 승인
    */
    @PatchMapping("/teacher/approval/accept/{teacherId}")
    public void acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/06/29 5:13 PM
     * 작성자: 이승범
     * 작성내용: 교사 해고
     */
    @PatchMapping("/teacher/fire/{teacherId}")
    public void fireTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.fireTeacher(userId, teacherId);
    }
}
