package FIS.iLUVit.domain.scrap.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.scrap.dto.*;
import FIS.iLUVit.domain.scrap.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 게시물 별 폴더 내 스크랩 유무 수정
     */
    @PutMapping("post/{postId}")
    public ResponseEntity<Void> updateScrapPost(@Login Long userId, @PathVariable("postId") Long postId, @Valid @RequestBody List<ScrapDirUpdateRequest> request) {
        scrapService.modifyScrapPost(userId, postId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 스크랩한 게시물 스크랩 취소
     */
    @DeleteMapping("post/{scrapPostId}")
    public ResponseEntity<Void> deleteScrapPost(@Login Long userId, @PathVariable("scrapPostId") Long scrapPostId) {
        scrapService.deleteScrapPost(userId, scrapPostId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스크랩 폴더 전체 조회
     */
    @GetMapping("dir")
    public ResponseEntity<List<ScrapDirResponse>> getAllScrapDir(@Login Long userId) {
        List<ScrapDirResponse> scrapDirResponses = scrapService.findScrapDirList(userId);
        return ResponseEntity.ok(scrapDirResponses);
    }

    /**
     * 스크랩 폴더 생성
     */
    @PostMapping("dir")
    public ResponseEntity<List<ScrapDirResponse>> createScrapDir(@Login Long userId, @Valid @RequestBody ScrapDirCreateRequest request) {
        List<ScrapDirResponse> responses = scrapService.saveNewScrapDir(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 스크랩 폴더 이름 수정
     */
    @PutMapping("dir/name")
    public ResponseEntity<Void> updateScrapDir(@Login Long userId, @Valid @RequestBody ScrapDirNameUpdateRequest request) {
        scrapService.modifyScrapDirName(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스크랩 폴더 삭제
     */
    @DeleteMapping("dir/{scrapDirId}")
    public ResponseEntity<List<ScrapDirResponse>> deleteScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId) {
        List<ScrapDirResponse> responses = scrapService.deleteScrapDir(userId, scrapDirId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 게시물 별 스크랩 폴더 전체 조회
     */
    @GetMapping("post/{postId}/dir")
    public ResponseEntity<List<ScrapDirFindByPostResponse>> getScrapDirByPost(@Login Long userId, @PathVariable("postId") Long postId) {
        List<ScrapDirFindByPostResponse> scrapDirFindByPostRespons = scrapService.findScrapDirListByPost(userId, postId);
        return ResponseEntity.ok(scrapDirFindByPostRespons);
    }

    /**
     * 스크랩 폴더별 게시물 전체 조회
     */
    @GetMapping("dir/{scrapDirId}/post")
    public ResponseEntity<Slice<ScrapDirPostsResponse>> getPostByScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId, Pageable pageable) {
        Slice<ScrapDirPostsResponse> scrapPostPreviewResponses = scrapService.findPostByScrapDir(userId, scrapDirId, pageable);
        return ResponseEntity.ok(scrapPostPreviewResponses);
    }

}