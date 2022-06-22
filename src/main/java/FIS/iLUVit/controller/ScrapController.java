package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
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
     * 작성날짜: 2022/06/21 2:11 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 목록 정보 가져오기
     */
    @GetMapping("/user/scrap/dir")
    public ScrapListInfoResponse ScrapListInfo(@Login Long id) {
        return scrapService.findScrapDirListInfo(id);
    }

    /**
     * 작성날짜: 2022/06/21 2:11 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 추가하기
     */
    @PostMapping("/user/scrap/dir")
    public ScrapListInfoResponse addScrap(@Login Long id, @RequestBody addScrapRequest request) {
        return scrapService.addScrapDir(id, request);
    }

    /**
     * 작성날짜: 2022/06/21 3:00 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 삭제하기
     */
    @DeleteMapping("/user/scrap/dir")
    public ScrapListInfoResponse deleteScrap(@Login Long id, @RequestParam Long scrapId) {
        return scrapService.deleteScrapDir(id, scrapId);
    }

    /**
     * 작성날짜: 2022/06/21 5:06 PM
     * 작성자: 이승범
     * 작성내용: 게시물 스크랩하기
     */
    @PostMapping("/user/scrap")
    public void scrapPost(@Login Long userId, @RequestBody scrapPostRequest request) {
        scrapService.scrapPost(userId, request);
    }

    /**
     * 작성날짜: 2022/06/22 10:24 AM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 이름 바꾸기
     */
    @PutMapping("/user/scrap/dir/name")
    public void updateScrapDirName(@Login Long id, @RequestBody updateScrapDirNameRequest request) {
        scrapService.updateScrapDirName(id, request);
    }

//    @DeleteMapping("/user/scrap/post")
//    public void deleteScrapPost(@Login Long id, @RequestParam Long scrapId, @RequestParam Long postId) {
//        scrapService.deleteScrapPost(id, scrapId, postId);
//    }
}
