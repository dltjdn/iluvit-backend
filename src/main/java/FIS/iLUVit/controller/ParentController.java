package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.parent.ParentDetailRequest;
import FIS.iLUVit.dto.parent.ParentDetailResponse;
import FIS.iLUVit.dto.parent.SignupParentRequest;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("parent")
public class ParentController {

    private final ParentService parentService;

    /**
     * PARENT
     */

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 학부모 프로필 조회
     */
    @GetMapping("")
    public ParentDetailResponse findParentDetail(@Login Long id) throws IOException {
        return parentService.findDetail(id);
    }

    /**
     * 작성날짜: 2022/05/16 10:18 AM
     * 작성자: 이승범
     * 작성내용: 학부모 정보 수정
     */
    @PostMapping("")
    public ParentDetailResponse updateParentDetail(@Login Long id, @Valid @ModelAttribute ParentDetailRequest request) throws IOException {
        return parentService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/06/23 4:46 PM
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    @PostMapping("signup")
    public void signupParent(@RequestBody @Valid SignupParentRequest request) {
        parentService.signup(request);
    }

}
