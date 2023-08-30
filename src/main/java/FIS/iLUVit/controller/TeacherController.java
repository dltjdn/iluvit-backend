package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.teacher.SignupTeacherRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailRequest;
import FIS.iLUVit.dto.teacher.TeacherDetailResponse;
import FIS.iLUVit.dto.teacher.TeacherInfoForAdminDto;
import FIS.iLUVit.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "교사 API")
@RequestMapping("teacher")
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * TEACHER
     */

    /**
     * 작성자: 이승범
     * 작성내용: 교사 정보 상세 조회
     */
    @Operation(summary = "교사 정보 상세 조회", description = "교사 정보를 조회합니다.")
    @GetMapping("")
    public TeacherDetailResponse getTeacherDetail(@Login Long id) throws IOException {
        return teacherService.findTeacherDetails(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 생성 (교사 회원가입)
     */
    @Operation(summary = "교사 생성 (교사 회원가입)", description = "교사로 회원가입합니다.")
    @PostMapping("signup")
    public void createTeacher(@RequestBody @Valid SignupTeacherRequest request) {
        teacherService.signupTeacher(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 정보 수정
     */
    @Operation(summary = "교사 정보 수정", description = "교사의 정보를 수정합니다.")
    @PostMapping("")
    public TeacherDetailResponse updateTeacher(@Login Long id, @Valid @ModelAttribute TeacherDetailRequest request) throws IOException {
        return teacherService.modifyTeacherInfo(id, request);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 교사 회원가입, 교사 이직용 시설 정보 조회
     */
    @Operation(summary = "교사 회원가입, 교사 이직용 시설 정보 조회", description = "교사가 회원가입을 할 때 근무등록을 원하는 시설의 정보를 검색합니다.")
    @GetMapping("search/center")
    public Slice<CenterDto> getCenterForTeacher(@ModelAttribute CenterRequest request, Pageable pageable) {
        return teacherService.findCenterForSignupTeacher(request, pageable);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설에 교사 시설 승인 요청하기
     */
    @Operation(summary = "교사 시설 승인 요청", description = "교사가 시설에 승인을 요청합니다.")
    @PatchMapping("center/{centerId}")
    public void assignCenterForTeacher(@Login Long userId, @PathVariable("centerId") Long centerId) {
        teacherService.requestAssignCenterForTeacher(userId, centerId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사의 시설 탈퇴하기
     */
    @Operation(summary = "교사의 시설 탈퇴", description = "교사가 등록된 시설을 탈퇴합니다.")
    @PatchMapping("center")
    public void leaveCenterForTeacher(@Login Long userId) {
        teacherService.leaveCenterForTeacher(userId);
    }


    /**
     * DIRECTOR
     */

    /**
     * 작성자: 이승범
     * 작성내용: 교사 시설 승인 페이지용 교사 정보 조회
     */
    @Operation(summary = "교사 정보 전체 조회", description = "시설 승인 요청을 보낸 교사의 정보를 조회합니다.")
    @GetMapping("approval")
    public List<TeacherInfoForAdminDto> getAllTeacher(@Login Long userId) {
        return teacherService.findTeacherApprovalList(userId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 시설 승인
     */
    @Operation(summary = "교사 시설 승인", description = "시설에 등록 요청을 보낸 교사를 승인합니다.")
    @PatchMapping("{teacherId}/accept")
    public void acceptTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.acceptTeacherRegistration(userId, teacherId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 교사 삭제/승인거절
     */
    @Operation(summary = "교사 시설 거절(삭제)", description = "시설에 등록하려는 교사의 요청을 거절합니다. 또는 관리교사가 교사를 삭제합니다.")
    @PatchMapping("{teacherId}/reject")
    public void rejectTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.rejectTeacherRegistration(userId, teacherId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 관리교사 권한 부여
     */
    @Operation(summary = "관리교사 권한 부여", description = "관리교사 권한을 부여합니다.")
    @PatchMapping("{teacherId}/mandate")
    public void mandateTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.mandateTeacher(userId, teacherId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 관리교사 권한 박탈
     */
    @Operation(summary = "관리교사 권한 박탈", description = "관리교사 권한을 박탈합니다.")
    @PatchMapping("{teacherId}/demote")
    public void demoteTeacher(@Login Long userId, @PathVariable("teacherId") Long teacherId) {
        teacherService.demoteTeacher(userId, teacherId);
    }

    /**
     *   작성자: 이서우
     *   작성내용: 교사 회원 탈퇴
     */
    @Operation(summary = "교사 회원 탈퇴", description = " 교사 회원 탈퇴를 진행합니다.")
    @DeleteMapping("withdraw")
    public Long deleteTeacher(@Login Long userId){
        return teacherService.withdrawTeacher(userId);
    }

}
