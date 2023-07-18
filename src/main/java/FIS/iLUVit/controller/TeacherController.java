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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 교사 정보 상세 조회
     */
    @GetMapping("")
    public ResponseEntity<TeacherDetailResponse> getTeacherDetail(@Login Long id) throws IOException {
        TeacherDetailResponse teacherDetailResponse = teacherService.findTeacherDetails(id);
        return ResponseEntity.ok(teacherDetailResponse);
    }

    /**
     * 교사 생성 (교사 회원가입)
     */
    @PostMapping("signup")
    public ResponseEntity<Void> createTeacher(@RequestBody @Valid SignupTeacherRequest request) {
        teacherService.signupTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 교사 정보 수정
     */
    @PostMapping("")
    public ResponseEntity<Void> updateTeacher(@Login Long id, @Valid @ModelAttribute TeacherDetailRequest request) throws IOException {
        teacherService.modifyTeacherInfo(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 교사 회원가입, 교사 이직용 시설 정보 조회
     */
    @GetMapping("search/center")
    public ResponseEntity<Slice<CenterDto>> getCenterForTeacher(@ModelAttribute CenterRequest request, Pageable pageable) {
        Slice<CenterDto> centerDtos = teacherService.findCenterForSignupTeacher(request, pageable);
        return ResponseEntity.ok(centerDtos);
    }

    /**
     * 시설에 교사 시설 승인 요청하기
     */
    @PatchMapping("center/{centerId}")
    public ResponseEntity<Void> assignCenterForTeacher(@Login Long userId, @PathVariable("centerId") Long centerId) {
        teacherService.requestAssignCenterForTeacher(userId, centerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 교사의 시설 탈퇴하기
     */
    @PatchMapping("center")
    public ResponseEntity<Void> leaveCenterForTeacher(@Login Long userId) {
        teacherService.leaveCenterForTeacher(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * DIRECTOR
     */

    /**
     * 교사 시설 승인 페이지용 교사 정보 조회
     */
    @GetMapping("approval")
    public ResponseEntity<List<TeacherInfoForAdminDto>> getAllTeacher(@Login Long userId) {
        List<TeacherInfoForAdminDto> teacherInfoForAdminDtos = teacherService.findTeacherApprovalList(userId);
        return ResponseEntity.ok(teacherInfoForAdminDtos);
    }

    /**
     * 교사 시설 승인
     */
    @PatchMapping("{teacherId}/accept")
    public ResponseEntity<Void> acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacherRegistration(userId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 교사 삭제/승인거절
     */
    @PatchMapping("{teacherId}/reject")
    public ResponseEntity<Void> rejectTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.rejectTeacherRegistration(userId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 관리교사 권한 부여
     */
    @PatchMapping("{teacherId}/mandate")
    public ResponseEntity<Void> mandateTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.mandateTeacher(userId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 관리교사 권한 박탈
     */
    @PatchMapping("{teacherId}/demote")
    public ResponseEntity<Void> demoteTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.demoteTeacher(userId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 교사 회원 탈퇴
     */
    @DeleteMapping("withdraw")
    public ResponseEntity<Void> deleteTeacher(@Login Long userId){
        teacherService.withdrawTeacher(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
