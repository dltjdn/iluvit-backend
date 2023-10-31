package FIS.iLUVit.domain.parent.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.parent.dto.ParentUpdateRequest;
import FIS.iLUVit.domain.parent.dto.ParentFindOneResponse;
import FIS.iLUVit.domain.parent.dto.ParentCreateRequest;
import FIS.iLUVit.domain.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     *  학부모 정보 상세 조회
     */
    @GetMapping("")
    public ResponseEntity<ParentFindOneResponse> getParentDetails(@Login Long userId){
        ParentFindOneResponse parentDetails = parentService.findParentDetails(userId);
        return ResponseEntity.ok(parentDetails);
    }

    /**
     *  학부모 정보 수정
     */
    @PatchMapping("")
    public ResponseEntity<ParentFindOneResponse> updateParent(@Login Long userId, @Valid @ModelAttribute ParentUpdateRequest parentUpdateRequest) throws IOException {
        ParentFindOneResponse response = parentService.modifyParentInfo(userId, parentUpdateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 학부모 생성 (학부모 회원가입)
     */
    @PostMapping("signup")
    public ResponseEntity<Void> createParent(@RequestBody @Valid ParentCreateRequest parentCreateRequest) {
        parentService.signupParent(parentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 학부모 회원 탈퇴
     */
    @DeleteMapping("withdraw")
    public ResponseEntity<Void> deleteParent(@Login Long userId){
        parentService.withdrawParent(userId);
        return ResponseEntity.noContent().build();
    }
}
