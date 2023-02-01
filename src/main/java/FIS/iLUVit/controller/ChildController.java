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
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    @GetMapping("info")
    public List<ChildDto> childInfo(@Login Long id) {
        return childService.childInfo(id);
    }

    /**
     * 작성날짜: 2022/06/23 5:24 PM
     * 작성자: 이승범
     * 작성내용: 아이 추가 (아이 정보 저장)
     */
    @PostMapping("")
    public void saveChild(@Login Long userId, @Valid @ModelAttribute ChildDetailRequest request) throws IOException {
        childService.saveChild(userId, request);
    }

    /**
     * 작성날짜: 2022/06/27 4:57 PM
     * 작성자: 이승범
     * 작성내용: 아이 정보 조회
     */
    @GetMapping("{childId}")
    public ChildDetailResponse findChildInfoDetail(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.findChildInfoDetail(userId, childId);
    }

    /**
     * 작성날짜: 2022/06/27 5:47 PM
     * 작성자: 이승범
     * 작성내용: 아이 정보 수정
     */
    @PutMapping("{childId}")
    public ChildDetailResponse updateChild(@Login Long userId, @PathVariable("childId") Long childId,
                                           @ModelAttribute ChildRequest request, Pageable pageable) throws IOException {
        return childService.updateChild(userId, childId, request);
    }

    /**
     * 작성날짜: 2022/06/28 3:17 PM
     * 작성자: 이승범
     * 작성내용: 아이 정보 삭제
     */
    @DeleteMapping("{childId}")
    public List<ChildDto> deleteChild(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.deleteChild(userId, childId);
    }

    /**
     *   작성날짜: 2022/06/24 10:29 AM
     *   작성자: 이승범
     *   작성내용: 아이 정보 저장 과정에서 필요한 시설 정보 가져오기
     */
    @GetMapping("search/center")
    public Slice<CenterDto> centerInfoForAddChild(@ModelAttribute CenterRequest request, Pageable pageable) {
        return childService.findCenterForAddChild(request, pageable);
    }

    /**
     * 작성날짜: 2022-08-09 오후 5:57
     * 작성자: 이승범
     * 작성내용: 학부모가 시설에 아이의 시설 승인을 요청
     */
    @PatchMapping("{childId}/center/{centerId}")
    public Long mappingCenter(@Login Long userId, @PathVariable("childId") Long childId, @PathVariable("centerId") Long centerId) {
        return childService.mappingCenter(userId, childId, centerId).getCenter().getId();
    }

    /**
     * 작성날짜: 2022/08/08 3:54 PM
     * 작성자: 이승범
     * 작성내용: 아이의 시설 탈퇴
     */
    @PatchMapping("{childId}/center")
    public void exitCenter(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.exitCenter(userId, childId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성날짜: 2022/06/30 10:36 AM
     * 작성자: 이승범
     * 작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
     */
    @GetMapping("approval")
    public List<ChildInfoForAdminDto> approvalList(@Login Long userId) {
        return childService.findChildApprovalInfoList(userId);
    }

    /**
     * 작성날짜: 2022/06/30 2:54 PM
     * 작성자: 이승범
     * 작성내용: 아이를 시설에 승인
     */
    @PatchMapping("{childId}/accept")
    public void acceptChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.acceptChild(userId, childId);
    }

    /**
     * 작성날짜: 2022/06/30 4:25 PM
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    @PatchMapping("{childId}/reject")
    public void fireChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.fireChild(userId, childId);
    }

}
