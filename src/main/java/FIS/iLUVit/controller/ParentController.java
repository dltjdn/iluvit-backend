package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final MessageSource messageSource;

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
    @PostMapping("/parent/detail")
    public ParentDetailResponse updateParentDetail(@Login Long id, @Valid @ModelAttribute ParentDetailRequest request) throws IOException {
        return parentService.updateDetail(id, request);
    }

    /**
     * 작성날짜: 2022/06/23 4:46 PM
     * 작성자: 이승범
     * 작성내용: 학부모 회원가입
     */
    @PostMapping("/signup/parent")
    public void signupParent(@RequestBody @Valid SignupParentRequest request) {
        parentService.signup(request);
    }

    /**
     * 작성날짜: 2022/07/01 5:08 PM
     * 작성자: 이승범
     * 작성내용: 시설 찜하기
     */
    @PostMapping("/parent/prefer/{centerId}")
    public void savePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        parentService.savePrefer(userId, centerId);
    }

    /**
     * 작성날짜: 2022/07/04 2:16 PM
     * 작성자: 이승범
     * 작성내용: 시설 찜 해제
     */
    @DeleteMapping("/parent/prefer/{centerId}")
    public void deletePrefer(@Login Long userId, @PathVariable("centerId") Long centerId) {
        parentService.deletePrefer(userId, centerId);
    }
}
