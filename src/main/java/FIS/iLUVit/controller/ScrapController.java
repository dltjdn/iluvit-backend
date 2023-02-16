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
     * 작성내용: 게시물 관련 스크랩 상태 수정
     */
    @PutMapping("post")
    public void scrapPost(@Login Long userId, @Valid @RequestBody ScrapByPostRequest request) {
        scrapService.scrapPost(userId, request);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 스크랩한 게시물 스크랩 취소
     */
    @DeleteMapping("post")
    public void deleteScrapPost(@Login Long userId, @RequestParam Long scrapPostId) {
        scrapService.deleteScrapPost(userId, scrapPostId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 목록 조회
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
     * 작성내용: 스크랩 폴더 이름 바꾸기
     */
    @PutMapping("dir/name")
    public void updateScrapDirName(@Login Long id, @Valid @RequestBody ScrapDirDetailRequest request) {
        scrapService.updateScrapDirName(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 삭제
     */
    @DeleteMapping("dir")
    public List<ScrapInfoDto>  deleteScrap(@Login Long userId, @RequestParam Long scrapId) {
        return scrapService.deleteScrapDir(userId, scrapId);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 게시물 관련 스크랩 폴더 목록 조회
     */
    @GetMapping("post/dir")
    public List<ScrapInfoByPostDto> ScrapListByPost(@Login Long userId, @RequestParam Long postId) {
        return scrapService.findScrapListByPost(userId, postId);
    }

    /**
     *   작성자: 이승범
     *   작성내용: 스크랩 폴더별 게시물 목록 조회
     */
    @GetMapping("dir/post")
    public Slice<ScrapPostPreviewResponse> searchPostsByScrap(@Login Long userId, @RequestParam Long scrapId, Pageable pageable) {
        return scrapService.searchByScrap(userId, scrapId, pageable);
    }

}
