package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    @GetMapping("/parent/childInfo")
    public ChildInfoDTO childInfo(@Login Long id) {
        return parentService.ChildrenInfo(id);
    }

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 정보
     */
    @GetMapping("/parent/detail")
    public ParentDetailResponse findParentDetail(@Login Long id) throws IOException {
        return parentService.findDetail(id);
    }

    /**
     * 작성날짜: 2022/05/16 10:18 AM
     * 작성자: 이승범
     * 작성내용: 부모의 마이페이지 수정
     */
    @PutMapping("/parent/detail")
    public ParentDetailResponse updateParentDetail(@Login Long id, @ModelAttribute ParentDetailRequest request) throws IOException {
        return parentService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/06/23 4:46 PM
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    @PostMapping("/signup/parent")
    public void signupParent(@RequestBody SignupParentRequest request) {
        parentService.signup(request);
    }

    /**
    *   작성날짜: 2022/06/23 5:24 PM
    *   작성자: 이승범
    *   작성내용: 아이 추가
    */
    @PostMapping("/parent/child")
    public void saveChild(@Login Long userId, @ModelAttribute SaveChildRequest request) {
        parentService.saveChild(userId, request);
    }
}
