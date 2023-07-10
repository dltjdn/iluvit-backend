package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
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
    public void updateScrapPost(@Login Long userId, @PathVariable("postId") Long postId, @Valid @RequestBody List<ScrapDirUpdateRequest> request) {
        scrapService.modifyScrapPost(userId, postId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩한 게시물 스크랩 취소
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
    public List<ScrapInfoDto> getAllScrapDir(@Login Long id) {
        return scrapService.findScrapDirList(id);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 생성
     */
    @PostMapping("dir")
    public List<ScrapInfoDto> createScrapDir(@Login Long id, @Valid @RequestBody ScrapDirRequest request) {
        return scrapService.saveNewScrapDir(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 이름 수정
     */
    @PutMapping("dir/name")
    public void updateScrapDir(@Login Long id, @Valid @RequestBody ScrapDirDetailRequest request) {
        scrapService.modifyScrapDirName(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 삭제
     */
    @DeleteMapping("dir/{scrapDirId}")
    public List<ScrapInfoDto>  deleteScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId) {
        return scrapService.deleteScrapDir(userId, scrapDirId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 게시물 별 스크랩 폴더 전체 조회
     */
    @GetMapping("post/{postId}/dir")
    public List<ScrapInfoByPostDto> getScrapDirByPost(@Login Long userId, @PathVariable("postId") Long postId) {
        return scrapService.findScrapDirListByPost(userId, postId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더별 게시물 전체 조회
     */
    @GetMapping("dir/{scrapDirId}/post")
    public Slice<ScrapPostPreviewResponse> getPostByScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId, Pageable pageable) {
        return scrapService.findPostByScrapDir(userId, scrapDirId, pageable);
    }

}
