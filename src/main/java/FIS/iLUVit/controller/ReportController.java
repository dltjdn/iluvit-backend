package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.report.ReportRequest;
import FIS.iLUVit.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "신고 API")
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
    @Operation(summary = "신고하기", description = "부적절한 게시글/댓글을 신고합니다.")
    @PostMapping("")
    public Long createReport(@Login Long userId, @RequestBody ReportRequest request){
        return reportService.registerReport(userId, request);
    }

}
