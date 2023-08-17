package FIS.iLUVit.service;

import FIS.iLUVit.dto.scrap.*;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ScrapErrorResult;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
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
     * 스크랩 폴더 목록 가져오기
     */
    public List<ScrapDirResponse> findScrapDirList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Scrap> scraps = scrapRepository.findByUser(user);
        List<ScrapDirResponse> scrapDirResponseList = new ArrayList<>();

        for (Scrap scrap : scraps) {
            scrapDirResponseList.add(new ScrapDirResponse(scrap));
        }
        return scrapDirResponseList;
    }

    /**
     * 스크랩 폴더 추가하기
     */
    public ScrapIdResponse saveNewScrapDir(Long userId, ScrapDirRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Scrap newScrap = Scrap.createScrap(user, request.getName());
        scrapRepository.save(newScrap);
        ScrapIdResponse scrapIdResponse = new ScrapIdResponse(newScrap.getId());

        // 스크랩 파일을 추가한 상태의 전체 스크랩 파일 목록 가져오기
        return scrapIdResponse;
    }

    /**
     * 스크랩 폴더 삭제하기
     */
    public void deleteScrapDir(Long userId, Long scrapId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Scrap scrapDir = scrapRepository.findByIdAndUser(scrapId, user)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP));

        // 조회된 스크랩 폴더가 기본 폴더인 경우 삭제 불가능
        if (scrapDir.getIsDefault()) {
            throw new ScrapException(ScrapErrorResult.CANT_DELETE_DEFAULT);
        }

        scrapRepository.delete(scrapDir);
    }

    /**
     * 스크랩 폴더 이름 바꾸기
     */
    public void modifyScrapDirName(Long userId, ScrapDirDetailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Scrap findScrap = scrapRepository.findByIdAndUser(request.getScrapId(), user)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP));

        // 조회된 스크랩 폴더의 이름을 요청된 dirName으로 수정
        findScrap.updateScrapDirName(request.getDirName());
    }

    /**
     * 게시물 스크랩하기
     */
    public void modifyScrapPost(Long userId, Long postId, List<ScrapDirUpdateRequest> scrapInfos) {
        // 사용자의 스크랩 폴더 리스트 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        List<Scrap> scraps = scrapRepository.findByUser(user);

        // 수정할 게시물 정보 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_POST));

        // request로 넘어온 스크랩 폴더 목록들을 사용자의 스크랩 폴더 목록과 비교
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
            // DB에서 가져온 사용자 스크랩 정보와 request 스크랩 정보가 일치하지 않는 경우
            if (!isFindScrap)
                throw new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP);
        });
    }

    /**
     * 스크랩한 게시물 스크랩 폴더에서 삭제
     */
    public void deleteScrapPost(Long userId, Long scrapPostId) {
        // 스크랩폴더에 해당 게시물의 저장정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        ScrapPost scrapPost = scrapPostRepository.findByIdAndScrapUser(scrapPostId, user)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.NOT_VALID_SCRAPPOST));

        scrapPostRepository.delete(scrapPost);
    }

    /**
     * 해당 게시물에 대한 스크랩 폴더 상태 목록 보여주기
     */
    public List<ScrapDirByPostResponse> findScrapDirListByPost(Long userId, Long postId) {
        // 사용자의 스크랩 폴더 리스트 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        List<Scrap> scrapListByUser = scrapRepository.findByUser(user);

        // 사용자의 모든 스크랩 폴더에 대해 ScrapDirByPostResponse 목록으로 변환
        return scrapListByUser.stream()
                .map(scrap -> new ScrapDirByPostResponse(scrap, postId))
                .collect(Collectors.toList());
    }

    /**
     * 해당 스크랩 폴더의 게시물들 preview 보여주기
     */
    public Slice<PostByScrapDirResponse> findPostByScrapDir(Long userId, Long scrapId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Slice<ScrapPost> scrapPosts = scrapPostRepository.findByScrapIdAndScrapUser(scrapId, user, pageable);

        // 조회된 ScrapPost 목록을 PostByScrapDirResponse로 변환하여 반환
        return scrapPosts.map(PostByScrapDirResponse::new);
    }

}