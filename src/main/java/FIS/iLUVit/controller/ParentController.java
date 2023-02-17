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
     * 작성자: 이승범
     * 작성내용: 학부모 정보 저장(학부모 회원가입)
     */
    @PostMapping("signup")
    public void createParent(@RequestBody @Valid SignupParentRequest request) {
        parentService.signup(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 정보 상세 조회
     */
    @GetMapping("")
    public ParentDetailResponse getParentDetails(@Login Long id) throws IOException {
        return parentService.findDetail(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 정보 수정
     */
    @PatchMapping("")
    public ParentDetailResponse updateParent(@Login Long id, @Valid @ModelAttribute ParentDetailRequest request) throws IOException {
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
