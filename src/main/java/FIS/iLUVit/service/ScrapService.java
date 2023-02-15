package FIS.iLUVit.service;

import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.exception.ScrapErrorResult;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.ScrapPostRepository;
import FIS.iLUVit.repository.ScrapRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<ScrapInfoDto>  findScrapDirListInfo(Long id) {
        List<Scrap> scraps = scrapRepository.findScrapsByUserWithScrapPosts(id);
        List<ScrapInfoDto> scrapInfoDtoList = new ArrayList<>();

        scraps.forEach(scrap -> {
            scrapInfoDtoList.add(new ScrapInfoDto(scrap));
        });
        return scrapInfoDtoList;
    }

    /**
     * 작성날짜: 2022/06/21 2:11 PM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 추가하기
     */
    public List<ScrapInfoDto>  addScrapDir(Long id, ScrapDirRequest request) {
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
    public List<ScrapInfoDto>  deleteScrapDir(Long userId, Long scrapId) {

        Scrap deletedScrap = scrapRepository.findScrapByIdAndUserId(scrapId, userId)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP));

        if (deletedScrap.getIsDefault()) {
            throw new ScrapException(ScrapErrorResult.CANT_DELETE_DEFAULT);
        }
        scrapRepository.delete(deletedScrap);

        return findScrapDirListInfo(userId);
    }

    /**
     * 작성날짜: 2022/06/22 10:24 AM
     * 작성자: 이승범
     * 작성내용: 스크랩 폴더 이름 바꾸기
     */
    public Scrap updateScrapDirName(Long id, ScrapDirDetailRequest request) {
        Scrap findScrap = scrapRepository.findScrapByIdAndUserId(request.getScrapId(), id)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP));
        findScrap.updateScrapDirName(request.getDirName());
        return findScrap;
    }

    /**
     * 작성날짜: 2022/06/21 5:06 PM
     * 작성자: 이승범
     * 작성내용: 게시물 스크랩하기
     */
    public List<Scrap> scrapPost(Long userId, Long postId, List<ScrapDirUpdateRequest> scrapInfos) {
        // 사용자의 스크랩폴더 목록을 가져온다.
        List<Scrap> scraps = scrapRepository.findScrapsByUserWithScrapPosts(userId);
        // 게시물 정보가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_POST));

        // request 스크랩 폴더 목록들을 사용자의 스크랩 폴더 목록과 비교
        scrapInfos.forEach(scrapInfo -> {
            boolean isFindScrap = false;
            for (Scrap scrap : scraps) {
                // 사용자의 스크랩 폴더와 request의 스크랩 폴더를 매칭
                if (Objects.equals(scrapInfo.getScrapId(), scrap.getId())) {
                    isFindScrap = true;
                    // 사용자의 스크랩 폴더에 해당 게시물이 존재하는지 검사
                    int scrapPostIndex = -1;
                    for (int i = 0; i < scrap.getScrapPosts().size(); i++) {
                        if (Objects.equals(scrap.getScrapPosts().get(i).getPost().getId(), post.getId())) {
                            scrapPostIndex = i;
                        }
                    }
                    // 이전에 스크랩 폴더에 게시물을 스크랩 하지 않았고 스크랩 해야되는 경우
                    if (scrapPostIndex == -1 && scrapInfo.getHasPost()) {
                        ScrapPost newScrapPost = ScrapPost.createScrapPost(post, scrap);
                        scrapPostRepository.save(newScrapPost);
                    } else if (scrapPostIndex != -1 && !scrapInfo.getHasPost()) {
                        // 이전에 해당 스크랩 폴더에 게시물을 스크랩 하였고 스크랩을 취소해야되는 경우
                        scrapPostRepository.delete(scrap.getScrapPosts().get(scrapPostIndex));
                    }
                    break;
                }
            }
            // db에서 가져온 사용자 스크랩 정보와 request 스크랩 정보가 일치하지 않는경우 예외처리
            if (!isFindScrap)
                throw new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP);
        });
        return scraps;
    }

    /**
     * 작성날짜: 2022/06/22 4:50 PM
     * 작성자: 이승범
     * 작성내용: 스크랩한 게시물 스크랩 폴더에서 삭제
     */
    public void deleteScrapPost(Long userId, Long scrapPostId) {
        // 스크랩폴더에 해당 게시물의 저장정보 조회
        ScrapPost scrapPost = scrapPostRepository.findByScrapAndPost(userId, scrapPostId)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAPPOST));
        scrapPostRepository.delete(scrapPost);
    }

    /**
     * 작성날짜: 2022/06/22 4:51 PM
     * 작성자: 이승범
     * 작성내용: 해당 게시물에 대한 스크랩폴더 상태 목록 보여주기
     */
    public List<ScrapInfoByPostDto> findScrapListByPost(Long userId, Long postId) {
        List<Scrap> scrapListByUser = scrapRepository.findScrapsByUserWithScrapPosts(userId);
        return scrapListByUser.stream()
                .map(scrap -> new ScrapInfoByPostDto(scrap, postId))
                .collect(Collectors.toList());
    }
    /**
     *   작성날짜: 2022/06/22 4:54 PM
     *   작성자: 이승범
     *   작성내용: 해당 스크랩 폴더의 게시물들 preview 보여주기
     */
    public Slice<ScrapPostPreviewResponse> searchByScrap(Long userId, Long scrapId, Pageable pageable) {
        Slice<ScrapPost> scrapPosts = scrapPostRepository.findByScrapWithPost(userId, scrapId, pageable);
        return scrapPosts.map(ScrapPostPreviewResponse::new);
    }
}
