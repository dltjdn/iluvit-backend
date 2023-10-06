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
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 스크랩 폴더 전체 조회
     */
    @GetMapping("dir")
    public ResponseEntity<List<ScrapDirResponse>> getAllScrapDir(@Login Long userId) {
        List<ScrapDirResponse> scrapDirResponses = scrapService.findScrapDirList(userId);
        return ResponseEntity.ok().body(scrapDirResponses);
    }

    /**
     * 스크랩 폴더 생성
     */
    @PostMapping("dir")
    public ResponseEntity<ScrapIdResponse> createScrapDir(@Login Long userId, @Valid @RequestBody ScrapDirRequest request) {
        ScrapIdResponse scrapIdResponse = scrapService.saveNewScrapDir(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scrapIdResponse);
    }

    /**
     * 스크랩 폴더 이름 수정
     */
    @PutMapping("dir/name")
    public ResponseEntity<Void> updateScrapDir(@Login Long userId, @Valid @RequestBody ScrapDirDetailRequest request) {
        scrapService.modifyScrapDirName(userId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 스크랩 폴더 삭제
     */
    @DeleteMapping("dir/{scrapDirId}")
    public ResponseEntity<Void> deleteScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId) {
        scrapService.deleteScrapDir(userId, scrapDirId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 게시물 별 스크랩 폴더 전체 조회
     */
    @GetMapping("post/{postId}/dir")
    public ResponseEntity<List<ScrapDirByPostResponse>> getScrapDirByPost(@Login Long userId, @PathVariable("postId") Long postId) {
        List<ScrapDirByPostResponse> scrapDirByPostResponses = scrapService.findScrapDirListByPost(userId, postId);
        return ResponseEntity.ok().body(scrapDirByPostResponses);
    }

    /**
     * 스크랩 폴더별 게시물 전체 조회
     */
    @GetMapping("dir/{scrapDirId}/post")
    public ResponseEntity<Slice<PostByScrapDirResponse>> getPostByScrapDir(@Login Long userId, @PathVariable("scrapDirId") Long scrapDirId, Pageable pageable) {
        Slice<PostByScrapDirResponse> scrapPostPreviewResponses = scrapService.findPostByScrapDir(userId, scrapDirId, pageable);
        return ResponseEntity.ok().body(scrapPostPreviewResponses);
    }

}