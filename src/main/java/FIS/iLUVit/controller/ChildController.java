package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterBasicResponse;
import FIS.iLUVit.dto.center.CenterBasicRequest;
import FIS.iLUVit.dto.child.*;
import FIS.iLUVit.service.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("child")
public class ChildController {

    private final ChildService childService;

    /**
     * PARENT
     */

    /**
     * 아이 정보 전체 조회
     */
    @GetMapping("info")
    public ResponseEntity<List<ChildDto>> getAllChild(@Login Long userId) {
        List<ChildDto> childDtos = childService.findChildList(userId);
        return ResponseEntity.ok(childDtos);
    }

    /**
     * 아이 정보 저장 ( 아이 생성 )
     */
    @PostMapping("")
    public ResponseEntity<Void> createChild(@Login Long userId, @Valid @ModelAttribute ChildDetailRequest childDetailRequest){
        childService.saveNewChild(userId, childDetailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 아이 정보 상세 조회
     */
    @GetMapping("{childId}")
    public ResponseEntity<ChildDetailResponse> getChildDetails(@Login Long userId, @PathVariable("childId") Long childId) {
        ChildDetailResponse childDetails = childService.findChildDetails(userId, childId);
        return ResponseEntity.ok(childDetails);
    }

    /**
     * 아이 정보 수정
     */
    @PutMapping("{childId}")
    public ResponseEntity<Void> updateChild(@Login Long userId, @PathVariable("childId") Long childId, @ModelAttribute ChildRequest childRequest)  {
       childService.modifyChildInfo(userId, childId, childRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 아이 삭제
     */
    @DeleteMapping("{childId}")
    public ResponseEntity<Void> deleteChild(@Login Long userId, @PathVariable("childId") Long childId) {
       childService.deleteChild(userId, childId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 아이 추가용 시설 정보 조회
     */
    @GetMapping("search/center")
    public ResponseEntity<Slice<CenterBasicResponse>> getCenterForChild(@ModelAttribute CenterBasicRequest centerBasicRequest, Pageable pageable) {
        Slice<CenterBasicResponse> centerDtos = childService.findCenterForAddChild(centerBasicRequest, pageable);
        return ResponseEntity.ok(centerDtos);
    }

    /**
     * 아이 시설 대기 ( 아이 시설 승인 요청 )
     */
    @PatchMapping("{childId}/center/{centerId}")
    public ResponseEntity<Void> assignCenterForChild(@Login Long userId, @PathVariable("childId") Long childId, @PathVariable("centerId") Long centerId) {
        childService.requestAssignCenterForChild(userId, childId, centerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 아이 시설 탈퇴
     */
    @PatchMapping("{childId}/center")
    public ResponseEntity<Void> leaveCenterForChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.leaveCenterForChild(userId, childId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * TEACHER
     */

    /**
     *  아이 승인용 아이 정보 전체 조회
     */
    @GetMapping("approval")
    public ResponseEntity<List<ChildInfoForAdminDto>> getChildForApproval(@Login Long userId) {
        List<ChildInfoForAdminDto> childApprovalList = childService.findChildApprovalList(userId);
        return ResponseEntity.ok(childApprovalList);
    }

    /**
     * 아이를 시설에 승인
     */
    @PatchMapping("{childId}/accept")
    public ResponseEntity<Void> acceptChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.acceptChildRegistration(userId, childId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 시설에서 아이 삭제/승인거절
     */
    @PatchMapping("{childId}/reject")
    public ResponseEntity<Void> rejectChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.rejectChildRegistration(userId, childId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
