package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.report.ReportRequest;
import FIS.iLUVit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;

    /**
     * COMMON
     */

    /**
     * 작성자: 최민아
     * 작성내용: 신고하기
     */
    @PostMapping("")
    public Long registerReport(@Login Long userId, @RequestBody ReportRequest request){
        return reportService.registerReport(userId, request);
    }

}
