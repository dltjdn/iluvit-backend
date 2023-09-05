package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.child.*;
import FIS.iLUVit.service.ChildService;
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
@Tag(name = "아이 API")
@RequestMapping("child")
public class ChildController {

    private final ChildService childService;

    /**
     * PARENT
     */

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 전체 조회
     */
    @Operation(summary = "아이 정보 전체 조회", description = "부모 메인페이지에 필요한 아이들의 정보를 조회합니다.")
    @GetMapping("info")
    public List<ChildDto> getAllChild(@Login Long id) {
        return childService.findChildList(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 저장
     */
    @Operation(summary = "아이 정보 저장", description = "아이를 추가합니다 (아이 정보 저장).")
    @PostMapping("")
    public void createChild(@Login Long userId, @Valid @ModelAttribute ChildDetailRequest request) throws IOException {
        childService.saveNewChild(userId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 상세 조회
     */
    @Operation(summary = "아이 정보 상세 조회", description = "아이의 정보를 상세 조회합니다.")
    @GetMapping("{childId}")
    public ChildDetailResponse getChildDetails(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.findChildDetails(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 수정
     */
    @Operation(summary = "아이 정보 수정", description = "아이 정보를 수정합니다.")
    @PutMapping("{childId}")
    public ChildDetailResponse updateChild(@Login Long userId, @PathVariable("childId") Long childId, @ModelAttribute ChildRequest request, Pageable pageable){
        return childService.modifyChildInfo(userId, childId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 삭제
     */
    @Operation(summary = "아이 정보 삭제", description = "아이 정보를 삭제합니다.")
    @DeleteMapping("{childId}")
    public List<ChildDto> deleteChild(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.deleteChild(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 추가용 시설 정보 조회
     */
    @Operation(summary = "아이 추가용 시설 정보 조회", description = "아이 정보를 저장하는 과정에서 필요한 시설 정보를 가져옵니다.")
    @GetMapping("search/center")
    public Slice<CenterDto> getCenterForChild(@ModelAttribute CenterRequest request, Pageable pageable) {
        return childService.findCenterForAddChild(request, pageable);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 시설 대기 ( 아이 시설 승인 요청 )
     */
    @Operation(summary = "아이 시설 대기 ( 아이 시설 승인 요청 )", description = "학부모가 시설에 아이의 시설 승인을 요청합니다.")
    @PatchMapping("{childId}/center/{centerId}")
    public Long assignCenterForChild(@Login Long userId, @PathVariable("childId") Long childId, @PathVariable("centerId") Long centerId) {
        return childService.requestAssignCenterForChild(userId, childId, centerId).getCenter().getId();
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 시설 탈퇴
     */
    @Operation(summary = "아이 시설 탈퇴", description = "아이의 시설을 탈퇴시킵니다.")
    @PatchMapping("{childId}/center")
    public void leaveCenterForChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.leaveCenterForChild(userId, childId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이승범
     * 작성내용: 아이 승인용 아이 정보 전체 조회
     */
    @Operation(summary = "아이 승인용 아이 정보 전체 조회", description = "아이 승인 페이지를 위한 시설에 등록된 아이들 정보를 조회합니다.")
    @GetMapping("approval")
    public List<ChildInfoForAdminDto> getChildForApproval(@Login Long userId) {
        return childService.findChildApprovalList(userId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이를 시설에 승인
     */
    @Operation(summary = "아이 시설 승인", description = "아이를 시설에 승인합니다.")
    @PatchMapping("{childId}/accept")
    public void acceptChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.acceptChildRegistration(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    @Operation(summary = "아이 시설 거절(삭제)", description = "시설에서 아이를 삭제/승인 거절을 합니다.")
    @PatchMapping("{childId}/reject")
    public void rejectChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.rejectChildRegistration(userId, childId);
    }

}
