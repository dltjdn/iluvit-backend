package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.SaveChildRequest;
import FIS.iLUVit.service.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    /**
     *   작성날짜: 2022/06/23 5:24 PM
     *   작성자: 이승범
     *   작성내용: 아이 추가
     */
    @PostMapping("/parent/child")
    public void saveChild(@Login Long userId, @ModelAttribute SaveChildRequest request) throws IOException {
        childService.saveChild(userId, request);
    }
}
