package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.report.ReportRequest;
import FIS.iLUVit.service.ReportService;
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
    public ResponseEntity<Void> createReport(@Login Long userId, @RequestBody ReportRequest request){
        reportService.registerReport(userId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
