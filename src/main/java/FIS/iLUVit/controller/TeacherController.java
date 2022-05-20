package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.TeacherDetailResponse;
import FIS.iLUVit.controller.dto.UpdateTeacherDetailRequest;
import FIS.iLUVit.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/teacher/detail")
    public TeacherDetailResponse findTeacherDetail(@Login Long id) throws IOException {
        return teacherService.findDetail(id);
    }

    @PutMapping("/teacher/detail")
    public TeacherDetailResponse updateTeacherDetail(@Login Long id, @ModelAttribute UpdateTeacherDetailRequest request) throws IOException {
        return teacherService.updateDetail(id, request);
    }
}
