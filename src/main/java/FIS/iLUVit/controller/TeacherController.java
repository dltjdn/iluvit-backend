package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.TeacherDetailResponse;
import FIS.iLUVit.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/teacher/detail")
    public TeacherDetailResponse findTeacherDetail(@Login Long id) {
        teacherService.findDetail(id);

    }
}
