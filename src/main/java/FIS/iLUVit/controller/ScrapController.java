package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.ScrapListInfoResponse;
import FIS.iLUVit.controller.dto.addScrapRequest;
import FIS.iLUVit.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    /**
    *   작성날짜: 2022/06/21 2:11 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 목록 정보 가져오기
    */
    @GetMapping("/user/scrap")
    public ScrapListInfoResponse ScrapListInfo(@Login Long id) {
        return scrapService.findScrapListInfo(id);
    }

    /**
    *   작성날짜: 2022/06/21 2:11 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 추가하기
    */
    @PostMapping("/user/scrap")
    public ScrapListInfoResponse addScrap(@Login Long id, @RequestBody addScrapRequest request) {
        return scrapService.addScrap(id, request);
    }

    /**
    *   작성날짜: 2022/06/21 3:00 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 삭제하기
    */
    @DeleteMapping("/user/scrap")
    public ScrapListInfoResponse deleteScrap(@Login Long id, @RequestParam Long scrapId) {
        return scrapService.deleteScrap(id, scrapId);
    }
}
