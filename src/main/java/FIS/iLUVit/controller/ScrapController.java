package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.service.ScrapService;
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
        return ResponseEntity.ok().build();
    }

    /**
     * 스크랩 폴더 전체 조회
     */
    @GetMapping("dir")
    public ResponseEntity<List<ScrapInfoDto>> getAllScrapDir(@Login Long id) {
        List<ScrapInfoDto> scrapInfoDtos = scrapService.findScrapDirList(id);
        return ResponseEntity.ok().body(scrapInfoDtos);
    }

    /**
     * 스크랩 폴더 생성
     */
    // TODO 프론트에게 문의 -> 불필요 시 응답객체 삭제
    @PostMapping("dir")
    public ResponseEntity<List<ScrapInfoDto>> createScrapDir(@Login Long id, @Valid @RequestBody ScrapDirRequest request) {
        List<ScrapInfoDto> scrapInfoDtos = scrapService.saveNewScrapDir(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(scrapInfoDtos);
    }

    /**
     * 스크랩 폴더 이름 수정
     */
    @PutMapping("dir/name")
    public ResponseEntity<Void> updateScrapDir(@Login Long id, @Valid @RequestBody ScrapDirDetailRequest request) {
        scrapService.modifyScrapDirName(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 스크랩 폴더 삭제
     */
    // TODO 프론트에게 문의 -> 불필요 시 응답객체 삭제
    @DeleteMapping("dir/{scrapDirId}")
    public ResponseEntity<List<ScrapInfoDto>>  deleteScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId) {
        List<ScrapInfoDto> scrapInfoDtos = scrapService.deleteScrapDir(userId, scrapDirId);
        return ResponseEntity.status(HttpStatus.OK).body(scrapInfoDtos);
    }

    /**
     * 게시물 별 스크랩 폴더 전체 조회
     */
    @GetMapping("post/{postId}/dir")
    public ResponseEntity<List<ScrapInfoByPostDto>> getScrapDirByPost(@Login Long userId, @PathVariable("postId") Long postId) {
        List<ScrapInfoByPostDto> scrapInfoByPostDtos = scrapService.findScrapDirListByPost(userId, postId);
        return ResponseEntity.ok().body(scrapInfoByPostDtos);
    }

    /**
     * 스크랩 폴더별 게시물 전체 조회
     */
    @GetMapping("dir/{scrapDirId}/post")
    public ResponseEntity<Slice<ScrapPostPreviewResponse>> getPostByScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId, Pageable pageable) {
        Slice<ScrapPostPreviewResponse> scrapPostPreviewResponses = scrapService.findPostByScrapDir(userId, scrapDirId, pageable);
        return ResponseEntity.ok().body(scrapPostPreviewResponses);
    }

}
