package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    @GetMapping("/parent/childInfo")
    public ChildInfoDTO childInfo(@Login Long id) {
        return childService.childrenInfo(id);
    }

    /**
     * 작성날짜: 2022/06/23 5:24 PM
     * 작성자: 이승범
     * 작성내용: 아이 추가
     */
    @PostMapping("/parent/child")
    public void saveChild(@Login Long userId, @ModelAttribute SaveChildRequest request) throws IOException {
        childService.saveChild(userId, request);
    }

    /**
     * 작성날짜: 2022/06/27 4:57 PM
     * 작성자: 이승범
     * 작성내용: 아이 프로필 조회
     */
    @GetMapping("/parent/child/{childId}")
    public ChildInfoDetailResponse findChildInfoDetail(@Login Long userId, @PathVariable("childId") Long childId, Pageable pageable) {
        return childService.findChildInfoDetail(userId, childId, pageable);
    }

    /**
     * 작성날짜: 2022/06/27 5:47 PM
     * 작성자: 이승범
     * 작성내용: 아이 프로필 수정
     */
    @PutMapping("/parent/child/{childId}")
    public ChildInfoDetailResponse updateChild(@Login Long userId, @PathVariable("childId") Long childId,
                                               @ModelAttribute UpdateChildRequest request, Pageable pageable) throws IOException {
        return childService.updateChild(userId, childId, request, pageable);
    }

    /**
     * 작성날짜: 2022/06/28 3:17 PM
     * 작성자: 이승범
     * 작성내용: 아이삭제
     */
    @DeleteMapping("/parent/child/{childId}")
    public ChildInfoDTO deleteChild(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.deleteChild(userId, childId);
    }

    /**
    *   작성날짜: 2022/06/30 10:36 AM
    *   작성자: 이승범
    *   작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
    */
    @GetMapping("/director/child/approval")
    public ChildApprovalListResponse approvalList(@Login Long userId) {
        return childService.findChildApprovalInfoList(userId);
    }
}
