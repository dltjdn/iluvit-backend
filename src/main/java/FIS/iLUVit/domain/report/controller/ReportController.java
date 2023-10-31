package FIS.iLUVit.domain.report.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.report.dto.ReportRequest;
import FIS.iLUVit.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 부적절한 게시글 혹은 댓글 신고하기
     */
    @PostMapping("")
    public ResponseEntity<Long> createReport(@Login Long userId, @RequestBody ReportRequest request){
        Long response = reportService.registerReport(userId, request);
        return ResponseEntity.ok(response);
    }

}
