package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ScrapListInfoResponse addScrapDir(Long id, AddScrapRequest request) {
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
    public ScrapListInfoResponse deleteScrapDir(Long userId, Long scrapId) {
        try {
            scrapRepository.deleteScrapById(scrapId);
        } catch (EmptyResultDataAccessException e) {
            throw new ScrapException("존재하지 않는 scrapId 입니다.");
        } catch (DataIntegrityViolationException e) {
            throw new ScrapException("존재하지 않는 postId 입니다.");
        }
        return findScrapDirListInfo(userId);
    }

    /**
     * 작성날짜: 2022/06/21 5:06 PM
     * 작성자: 이승범
     * 작성내용: 게시물 스크랩하기
     */
    public void scrapPost(Long userId, updateScrapByPostRequest request) {
        // 사용자의 스크랩폴더 목록을 가져온다.
        List<Scrap> scraps = scrapRepository.findScrapsByUserAndPostWithScrapPost(userId);

        // request 스크랩 폴더 목록들을 사용자의 스크랩 폴더 목록과 비교
        request.getScrapList().forEach(requestScrap -> {
            boolean isFindScrap = false;
            for (Scrap s : scraps) {
                // 사용자의 스크랩 폴더와 request의 스크랩 폴더를 매칭
                if (Objects.equals(requestScrap.getScrapId(), s.getId())) {
                    isFindScrap = true;
                    Post post = postRepository.getById(request.getPostId());
                    // 사용자의 스크랩 폴더에 해당 게시물이 존재하는지 검사
                    int scrapPostIndex = -1;
                    for (int i = 0; i < s.getScrapPosts().size(); i++) {
                        if (Objects.equals(s.getScrapPosts().get(i).getPost().getId(), post.getId())) {
                            scrapPostIndex = i;
                        }
                    }
                    // 이전에 스크랩 폴더에 게시물을 스크랩 하지 않았고 스크랩 해야되는 경우
                    if (scrapPostIndex == -1 && requestScrap.getHasPost()) {
                        ScrapPost newScrapPost = ScrapPost.createScrapPost(post, s);
                        scrapPostRepository.save(newScrapPost);
                    } else if (scrapPostIndex != -1 && !requestScrap.getHasPost()) {
                        // 이전에 해당 스크랩 폴더에 게시물을 스크랩 하였고 스크랩을 취소해야되는 경우
                        scrapPostRepository.delete(s.getScrapPosts().get(scrapPostIndex));
                    }
                    break;
                }
            }
            // db에서 가져온 사용자 스크랩 정보와 request 스크랩 정보가 일치하지 않는경우 예외처리
            if (!isFindScrap)
                throw new ScrapException("스크랩아이디가 존재하지 않거나 다른 사용자의 스크랩아이디입니다.");
        });
    }

    /**
     * 작성날짜: 2022/06/22 10:24 AM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 이름 바꾸기
     */
    public void updateScrapDirName(Long id, updateScrapDirNameRequest request) {
        Scrap findScrap = scrapRepository.findScrapByIdAndUserId(request.getScrapId(), id)
                .orElseThrow(() -> new ScrapException("userId 또는 scrapId가 잘못되었습니다."));
        findScrap.updateScrapDirName(request.getDirName());
    }

    /**
     * 작성날짜: 2022/06/22 4:50 PM
     * 작성자: 이승범
     * 작성내용: 스크랩한 게시물 스크랩 폴더에서 삭제
     */
    public void deleteScrapPost(Long userId, Long scrapPostId) {
        ScrapPost scrapPost = scrapPostRepository.findByScrapAndPost(userId, scrapPostId)
                .orElseThrow(() -> new ScrapException("유효하지 않은 scrapPostId 입니다."));
        scrapPostRepository.delete(scrapPost);
    }

    /**
     * 작성날짜: 2022/06/22 4:51 PM
     * 작성자: 이승범
     * 작성내용: 해당 게시물에 대한 스크랩폴더 상태 목록 보여주기
     */
    public ScrapListByPostResponse findScrapListByPost(Long userId, Long postId) {
        List<Scrap> scrapListByUser = scrapRepository.findScrapListByUser(userId);
        return new ScrapListByPostResponse(scrapListByUser.stream()
                .map(s -> new ScrapListByPostResponse.ScrapInfoByPost(s, postId))
                .collect(Collectors.toList()));
    }
}
