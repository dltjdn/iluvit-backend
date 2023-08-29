package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.parent.ParentDetailRequest;
import FIS.iLUVit.dto.parent.ParentDetailResponse;
import FIS.iLUVit.dto.parent.SignupParentRequest;
import FIS.iLUVit.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "학부모 API")
@RequestMapping("parent")
public class ParentController {

    private final ParentService parentService;

    /**
     * PARENT
     */

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 정보 상세 조회
     */
    @Operation(summary = "학부모 정보 상세 조회", description = "학부모의 상세 정보를 조회합니다.")
    @GetMapping("")
    public ParentDetailResponse getParentDetails(@Login Long id) throws IOException {
        return parentService.findParentDetails(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 정보 수정
     */
    @Operation(summary = "학부모 정보 수정", description = "학부모 정보를 수정합니다.")
    @PatchMapping("")
    public ParentDetailResponse updateParent(@Login Long id, @Valid @ModelAttribute ParentDetailRequest request) throws IOException {
        return parentService.modifyParentInfo(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모 생성 (학부모 회원가입)
     */
    @Operation(summary = "학부모 생성 ( 회원가입 ) ", description = "학부모로 회원가입 합니다.")
    @PostMapping("signup")
    public void createParent(@RequestBody @Valid SignupParentRequest request) {
        parentService.signupParent(request);
    }

    /**
     *   작성자: 이서우
     *   작성내용: 학부모 회원 탈퇴
     */
    @Operation(summary = "학부모 회원 탈퇴", description = "학부모 회원 탈퇴를 진행합니다.")
    @DeleteMapping("withdraw")
    public Long deleteParent(@Login Long userId){
        return parentService.withdrawParent(userId);
    }
}
