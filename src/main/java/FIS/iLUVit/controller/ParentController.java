package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.service.ParentService;
import FIS.iLUVit.service.dto.ChildInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/parent/child")
    public List<ChildInfo> childInfo(@Login Long id){
        return parentService.ChildrenInfo(id);
    }


}
