package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("scrap")
public class ScrapController {

    private final ScrapService scrapService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이승범
     * 작성내용: 게시물 별 폴더 내 스크랩 유무 수정
     */
    @PutMapping("post/{postId}")
    public void scrapPost(@Login Long userId, @PathVariable("postId") Long postId, @Valid @RequestBody List<ScrapDirUpdateRequest> request) {
        scrapService.scrapPost(userId, postId, request);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 스크랩 삭제(취소)
     */
    @DeleteMapping("post/{scrapPostId}")
    public void deleteScrapPost(@Login Long userId, @PathVariable("scrapPostId") Long scrapPostId) {
        scrapService.deleteScrapPost(userId, scrapPostId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 전체 조회
     */
    @GetMapping("dir")
    public List<ScrapInfoDto> ScrapListInfo(@Login Long id) {
        return scrapService.findScrapDirListInfo(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 생성
     */
    @PostMapping("dir")
    public List<ScrapInfoDto>  addScrap(@Login Long id, @Valid @RequestBody ScrapDirRequest request) {
        return scrapService.addScrapDir(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 이름 수정
     */
    @PutMapping("dir/name")
    public void updateScrapDirName(@Login Long id, @Valid @RequestBody ScrapDirDetailRequest request) {
        scrapService.updateScrapDirName(id, request);
    }

    /**
     * 작성날짜: 2022/06/21 3:00 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 삭제
     */
    @DeleteMapping("dir/{scrapDirId}")
    public List<ScrapInfoDto>  deleteScrap(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId) {
        return scrapService.deleteScrapDir(userId, scrapDirId);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 게시물 별 스크랩 폴더 전체 조회
     */
    @GetMapping("post/{postId}/dir")
    public List<ScrapInfoByPostDto> ScrapListByPost(@Login Long userId, @PathVariable("postId") Long postId) {
        return scrapService.findScrapListByPost(userId, postId);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 스크랩 폴더별 게시물 전체 조회
     */
    @GetMapping("dir/{scrapDirId}/post")
    public Slice<ScrapPostPreviewResponse> searchPostsByScrap(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId, Pageable pageable) {
        return scrapService.searchByScrap(userId, scrapDirId, pageable);
    }

}
