package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.child.*;
import FIS.iLUVit.service.ChildService;
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
    @GetMapping("info")
    public List<ChildDto> getAllChild(@Login Long id) {
        return childService.childInfo(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 저장
     */
    @PostMapping("")
    public void createChild(@Login Long userId, @Valid @ModelAttribute ChildDetailRequest request) throws IOException {
        childService.saveChild(userId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 상세 조회
     */
    @GetMapping("{childId}")
    public ChildDetailResponse getChildDetails(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.findChildInfoDetail(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 수정
     */
    @PutMapping("{childId}")
    public ChildDetailResponse updateChild(@Login Long userId, @PathVariable("childId") Long childId,
                                           @ModelAttribute ChildRequest request, Pageable pageable) throws IOException {
        return childService.updateChild(userId, childId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 정보 삭제
     */
    @DeleteMapping("{childId}")
    public List<ChildDto> deleteChild(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.deleteChild(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 추가용 시설 정보 조회
     */
    @GetMapping("search/center")
    public Slice<CenterDto> getCenterForChild(@ModelAttribute CenterRequest request, Pageable pageable) {
        return childService.findCenterForAddChild(request, pageable);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 시설 대기
     */
    @PatchMapping("{childId}/center/{centerId}")
    public Long assignCenterForChild(@Login Long userId, @PathVariable("childId") Long childId, @PathVariable("centerId") Long centerId) {
        return childService.mappingCenter(userId, childId, centerId).getCenter().getId();
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 시설 탈퇴
     */
    @PatchMapping("{childId}/center")
    public void leaveCenterForChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.exitCenter(userId, childId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이승범
     * 작성내용: 아이 승인용 아이 정보 전체 조회
     */
    @GetMapping("approval")
    public List<ChildInfoForAdminDto> getChildForApproval(@Login Long userId) {
        return childService.findChildApprovalInfoList(userId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이를 시설에 승인
     */
    @PatchMapping("{childId}/accept")
    public void acceptChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.acceptChild(userId, childId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    @PatchMapping("{childId}/reject")
    public void rejectChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.fireChild(userId, childId);
    }

}
