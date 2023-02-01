package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminDto;
import FIS.iLUVit.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("teacher")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * TEACHER
     */

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 교사 정보 조회
     */
    @GetMapping("")
    public TeacherDetailResponse findTeacherDetail(@Login Long id) throws IOException {
        return teacherService.findDetail(id);
    }

    /**
     * 작성날짜: 2022/05/20 4:43 PM
     * 작성자: 이승범
     * 작성내용: 교사 정보 저장
     */
    @PostMapping("")
    public TeacherDetailResponse updateTeacherDetail(@Login Long id, @Valid @ModelAttribute TeacherDetailRequest request) throws IOException {
        return teacherService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/05/24 5:24 PM
     * 작성자: 이승범
     * 작성내용: 교사 회원가입
     */
    @PostMapping("signup")
    public void signup(@RequestBody @Valid SignupTeacherRequest request) {
        teacherService.signup(request);
    }

    /**
     *   작성날짜: 2022/06/20 3:49 PM
     *   작성자: 이승범
     *   작성내용: 회원가입, 이직 과정에서 center 정보 가져오기
     */
    @GetMapping("search/center")
    public Slice<CenterDto> centerInfoForSignup(@ModelAttribute CenterRequest request, Pageable pageable) {
        return teacherService.findCenterForSignup(request, pageable);
    }

    /**
     * 작성날짜: 2022/06/30 11:59 AM
     * 작성자: 이승범
     * 작성내용: 시설에 교사 시설 승인 요청하기
     */
    @PatchMapping("center/{centerId}")
    public void assignCenter(@Login Long userId, @PathVariable("centerId") Long centerId) {
        teacherService.assignCenter(userId, centerId);
    }

    /**
     * 작성날짜: 2022/06/30 11:41 AM
     * 작성자: 이승범
     * 작성내용: 교사의 시설 탈퇴하기
     */
    @PatchMapping("center")
    public void escapeCenter(@Login Long userId) {
        teacherService.escapeCenter(userId);
    }


    /**
     * DIRECTOR
     */

    /**
     * 작성날짜: 2022/06/29 11:31 AM
     * 작성자: 이승범
     * 작성내용: 교사 시설 승인 페이지용 교사 정보 조회
     */
    @GetMapping("approval")
    public List<TeacherInfoForAdminDto> teacherApprovalList(@Login Long userId) {
        return teacherService.findTeacherApprovalList(userId);
    }

    /**
     * 작성날짜: 2022/06/29 11:32 AM
     * 작성자: 이승범
     * 작성내용: 교사 시설 승인
     */
    @PatchMapping("{teacherId}/accept")
    public void acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/06/29 5:13 PM
     * 작성자: 이승범
     * 작성내용: 교사 삭제/승인거절
     */
    @PatchMapping("{teacherId}/reject")
    public void fireTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.fireTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/07/01 3:07 PM
     * 작성자: 이승범
     * 작성내용: 관리교사 권한 부여
     */
    @PatchMapping("{teacherId}/mandate")
    public void mandateTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.mandateTeacher(userId, teacherId);
    }

    /**
     * 작성날짜: 2022/07/29 5:05 PM
     * 작성자: 이승범
     * 작성내용: 관리교사 권한 박탈
     */
    @PatchMapping("{teacherId}/demote")
    public void demoteTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.demoteTeacher(userId, teacherId);
    }

}
