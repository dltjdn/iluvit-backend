package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ScrapListInfoResponse;
import FIS.iLUVit.controller.dto.addScrapRequest;
import FIS.iLUVit.controller.dto.scrapPostRequest;
import FIS.iLUVit.controller.dto.updateScrapDirNameRequest;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.ScrapPostRepository;
import FIS.iLUVit.repository.ScrapRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ScrapPostRepository scrapPostRepository;

    /**
     * 작성날짜: 2022/06/21 2:03 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 목록 가져오기
     */
    public ScrapListInfoResponse findScrapDirListInfo(Long id) {
        List<Scrap> scraps = scrapRepository.findScrapsWithScrapPostsByUser(id);
        ScrapListInfoResponse response = new ScrapListInfoResponse();

        scraps.forEach(scrap -> {
            response.getData().add(new ScrapListInfoResponse.ScrapInfo(scrap));
        });
        return response;
    }

    /**
     * 작성날짜: 2022/06/21 2:11 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 추가하기
     */
    public ScrapListInfoResponse addScrapDir(Long id, addScrapRequest request) {
        User user = userRepository.getById(id);
        Scrap newScrap = Scrap.createScrap(user, request.getName());
        scrapRepository.save(newScrap);
        // 스크랩 파일을 추가한 상태의 전체 스크랩 파일 목록 가져오기
        return findScrapDirListInfo(id);
    }

    /**
     * 작성날짜: 2022/06/21 2:59 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 삭제하기
     */
    public ScrapListInfoResponse deleteScrapDir(Long id, Long scrapId) {
        try {
            scrapRepository.deleteById(scrapId);
        } catch (EmptyResultDataAccessException e) {
            throw new ScrapException("존재하지 않는 scrapId 입니다.");
        }
        return findScrapDirListInfo(id);
    }

    /**
     * 작성날짜: 2022/06/21 5:06 PM
     * 작성자: 이승범
     * 작성내용: 게시물 스크랩하기
     */
    public void scrapPost(Long userId, scrapPostRequest request) {
        scrapRepository.findScrapByIdAndUserId(request.getScrapId(), userId)
                .orElseThrow(() -> new ScrapException("잘못된 스크랩폴더로의 접근입니다."));

        Post post = postRepository.getById(request.getPostId());
        Scrap scrap = scrapRepository.getById(request.getScrapId());
        ScrapPost scrapPost = ScrapPost.createScrapPost(post, scrap);
        scrapPostRepository.save(scrapPost);
    }

    /**
    *   작성날짜: 2022/06/22 10:24 AM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 이름 바꾸기
    */
    public void updateScrapDirName(Long id, updateScrapDirNameRequest request) {
        Scrap findScrap = scrapRepository.findScrapByIdAndUserId(request.getScrapId(), id)
                .orElseThrow(() -> new ScrapException("userId 또는 scrapId가 잘못되었습니다."));
        findScrap.updateScrapDirName(request.getDirName());
    }

//    public void deleteScrapPost(Long id, Long scrapId, Long postId) {
//        scrapRepository.findByScrapAndPost(id, scrapId, postId);
//    }
}
