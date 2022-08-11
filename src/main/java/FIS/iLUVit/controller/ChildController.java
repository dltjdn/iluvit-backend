package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public void saveChild(@Login Long userId, @Valid @ModelAttribute SaveChildRequest request) throws IOException {
        childService.saveChild(userId, request);
    }

    /**
     * 작성날짜: 2022/06/27 4:57 PM
     * 작성자: 이승범
     * 작성내용: 아이 프로필 조회
     */
    @GetMapping("/parent/child/{childId}")
    public ChildInfoDetailResponse findChildInfoDetail(@Login Long userId, @PathVariable("childId") Long childId) {
        return childService.findChildInfoDetail(userId, childId);
    }

    /**
     * 작성날짜: 2022/06/27 5:47 PM
     * 작성자: 이승범
     * 작성내용: 아이 프로필 수정
     */
    @PutMapping("/parent/child/{childId}")
    public ChildInfoDetailResponse updateChild(@Login Long userId, @PathVariable("childId") Long childId,
                                               @ModelAttribute UpdateChildRequest request, Pageable pageable) throws IOException {
        return childService.updateChild(userId, childId, request);
    }

    /**
     * 작성날짜: 2022-08-09 오후 5:57
     * 작성자: 이승범
     * 작성내용: 학부모/아이의 시설 승인 요청
     */
    @PatchMapping("/parent/child/center/{childId}/{centerId}")
    public Long mappingCenter(@Login Long userId, @PathVariable("childId") Long childId, @PathVariable("centerId") Long centerId) {
        return childService.mappingCenter(userId, childId, centerId).getCenter().getId();
    }

    /**
     * 작성날짜: 2022/08/08 3:54 PM
     * 작성자: 이승범
     * 작성내용: 아이의 시설 탈퇴
     */
    @DeleteMapping("/parent/child/center/{childId}")
    public void exitCenter(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.exitCenter(userId, childId);
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
     * 작성날짜: 2022/06/30 10:36 AM
     * 작성자: 이승범
     * 작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
     */
    @GetMapping("/teacher/child/approval")
    public ChildApprovalListResponse approvalList(@Login Long userId) {
        return childService.findChildApprovalInfoList(userId);
    }

    /**
     * 작성날짜: 2022/06/30 2:54 PM
     * 작성자: 이승범
     * 작성내용: 아이 시설에 승인
     */
    @PatchMapping("/teacher/child/accept/{childId}")
    public void acceptChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.acceptChild(userId, childId);
    }

    /**
     * 작성날짜: 2022/06/30 4:25 PM
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    @PatchMapping("/teacher/child/fire/{childId}")
    public void fireChild(@Login Long userId, @PathVariable("childId") Long childId) {
        childService.fireChild(userId, childId);
    }
}
