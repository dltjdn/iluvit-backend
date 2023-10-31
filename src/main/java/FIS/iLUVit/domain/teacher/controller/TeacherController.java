package FIS.iLUVit.domain.teacher.controller;

import FIS.iLUVit.domain.teacher.dto.*;
import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.center.dto.CenterFindForUserResponse;
import FIS.iLUVit.domain.center.dto.CenterFindForUserRequest;
import FIS.iLUVit.domain.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<TeacherFindOneDetailResponse> getTeacherDetail(@Login Long userId) {
        TeacherFindOneDetailResponse teacherBasicInfoResponse = teacherService.findTeacherDetails(userId);
        return ResponseEntity.ok(teacherBasicInfoResponse);
    }

    /**
     * 교사 생성 (교사 회원가입)
     */
    @PostMapping("signup")
    public ResponseEntity<Void> createTeacher(@RequestBody @Valid TeacherSignupRequest request) {
        teacherService.signupTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 교사 정보 수정
     */
    @PostMapping("")
    public ResponseEntity<TeacherFindOneDetailResponse> updateTeacherInfo(@Login Long userId, @Valid @ModelAttribute TeacherUpdateRequest request){
        TeacherFindOneDetailResponse response = teacherService.updateTeacherInfo(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 교사 회원가입, 교사 이직용 시설 정보 조회
     */
    @GetMapping("search/center")
    public ResponseEntity<Slice<CenterFindForUserResponse>> getCenterForTeacher(@ModelAttribute CenterFindForUserRequest request, Pageable pageable) {
        Slice<CenterFindForUserResponse> centerDtos = teacherService.findCenterForSignupTeacher(request, pageable);
        return ResponseEntity.ok(centerDtos);
    }

    /**
     * 시설에 교사 시설 승인 요청하기
     */
    @PatchMapping("center/{centerId}")
    public ResponseEntity<Void> assignCenterForTeacher(@Login Long userId, @PathVariable("centerId") Long centerId) {
        teacherService.requestAssignCenterForTeacher(userId, centerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 교사의 시설 탈퇴하기
     */
    @PatchMapping("center")
    public ResponseEntity<Void> leaveCenterForTeacher(@Login Long userId) {
        teacherService.leaveCenterForTeacher(userId);
        return ResponseEntity.noContent().build();
    }


    /**
     * DIRECTOR
     */

    /**
     * 교사 시설 승인 페이지용 교사 정보 조회
     */
    @GetMapping("approval")
    public ResponseEntity<List<TeacherInfoForAdminResponse>> getAllTeacher(@Login Long userId) {
        List<TeacherInfoForAdminResponse> teacherInfoForAdminResponses = teacherService.findTeacherApprovalList(userId);
        return ResponseEntity.ok(teacherInfoForAdminResponses);
    }

    /**
     * 교사 시설 승인
     */
    @PatchMapping("{teacherId}/accept")
    public ResponseEntity<Void> acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacherRegistration(userId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 교사 삭제/승인거절
     */
    @PatchMapping("{teacherId}/reject")
    public ResponseEntity<Void> rejectTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.rejectTeacherRegistration(userId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리교사 권한 부여
     */
    @PatchMapping("{teacherId}/mandate")
    public ResponseEntity<Void> mandateTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.mandateTeacher(userId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 관리교사 권한 박탈
     */
    @PatchMapping("{teacherId}/demote")
    public ResponseEntity<Void> demoteTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.demoteTeacher(userId, teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 교사 회원 탈퇴
     */
    @DeleteMapping("withdraw")
    public ResponseEntity<Void> deleteTeacher(@Login Long userId){
        teacherService.withdrawTeacher(userId);
        return ResponseEntity.noContent().build();
    }

}
