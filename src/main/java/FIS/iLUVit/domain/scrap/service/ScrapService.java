package FIS.iLUVit.domain.scrap.service;

import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.post.exception.PostErrorResult;
import FIS.iLUVit.domain.post.exception.PostException;
import FIS.iLUVit.domain.scrap.domain.Scrap;
import FIS.iLUVit.domain.scrap.domain.ScrapPost;
import FIS.iLUVit.domain.scrap.dto.*;
import FIS.iLUVit.domain.scrap.exception.ScrapErrorResult;
import FIS.iLUVit.domain.scrap.exception.ScrapException;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.post.repository.PostRepository;
import FIS.iLUVit.domain.scrap.repository.ScrapPostRepository;
import FIS.iLUVit.domain.scrap.repository.ScrapRepository;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
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
        User user = getUser(userId);

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
        User user = getUser(userId);

        Scrap newScrap = Scrap.of(user, request.getName());
        scrapRepository.save(newScrap);
        ScrapIdResponse scrapIdResponse = new ScrapIdResponse(newScrap.getId());

        // 스크랩 파일을 추가한 상태의 전체 스크랩 파일 목록 가져오기
        return scrapIdResponse;
    }

    /**
     * 스크랩 폴더 삭제하기
     */
    public void deleteScrapDir(Long userId, Long scrapId) {
        User user = getUser(userId);
        Scrap scrapDir = getScrap(scrapId, user);

        // 조회된 스크랩 폴더가 기본 폴더인 경우 삭제 불가능
        if (scrapDir.getIsDefault()) {
            throw new ScrapException(ScrapErrorResult.CANNOT_DELETE_DEFAULT);
        }

        scrapRepository.delete(scrapDir);
    }


    /**
     * 스크랩 폴더 이름 바꾸기
     */
    public void modifyScrapDirName(Long userId, ScrapDirDetailRequest request) {
        User user = getUser(userId);
        Scrap findScrap = getScrap(request.getScrapId(), user);

        // 조회된 스크랩 폴더의 이름을 요청된 dirName으로 수정
        findScrap.updateScrapDirName(request.getDirName());
    }

    /**
     * 게시물 스크랩하기
     */
    public void modifyScrapPost(Long userId, Long postId, List<ScrapDirUpdateRequest> scrapInfos) {
        // 사용자의 스크랩 폴더 리스트 가져오기
        User user = getUser(userId);
        List<Scrap> scraps = scrapRepository.findByUser(user);

        // 수정할 게시물 정보 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        // request로 넘어온 스크랩 폴더 목록들을 사용자의 스크랩 폴더 목록과 비교
        scrapInfos.forEach(scrapInfo -> {
            for (Scrap scrap : scraps) {
                // 사용자의 스크랩 폴더와 request의 스크랩 폴더를 매칭
                if (!Objects.equals(scrapInfo.getScrapId(), scrap.getId())) {
                    throw new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP);
                }

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
            }

        });
    }

    /**
     * 스크랩한 게시물 스크랩 폴더에서 삭제
     */
    public void deleteScrapPost(Long userId, Long scrapPostId) {
        // 스크랩폴더에 해당 게시물의 저장정보 조회
        User user = getUser(userId);

        ScrapPost scrapPost = scrapPostRepository.findByIdAndScrapUser(scrapPostId, user)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.SCRAP_NOT_FOUND));

        scrapPostRepository.delete(scrapPost);
    }

    /**
     * 해당 게시물에 대한 스크랩 폴더 상태 목록 보여주기
     */
    public List<ScrapDirByPostResponse> findScrapDirListByPost(Long userId, Long postId) {
        // 사용자의 스크랩 폴더 리스트 가져오기
        User user = getUser(userId);
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
        User user = getUser(userId);
        Slice<ScrapPost> scrapPosts = scrapPostRepository.findByScrapIdAndScrapUser(scrapId, user, pageable);

        // 조회된 ScrapPost 목록을 PostByScrapDirResponse로 변환하여 반환
        return scrapPosts.map(PostByScrapDirResponse::new);
    }

    /**
     * 해당 시설과 연관된 사용자의 스크랩 게시물을 삭제합니다
     */
    public void deleteScrapByCenter(Teacher teacher){
        List<ScrapPost> scraps = scrapPostRepository.findByPostBoardCenter(teacher.getCenter());
        scrapPostRepository.deleteAll(scraps);
    }

    /**
     *  기본 스크랩 폴더 생성
     */
    public void saveDefaultSrap(User user){
        Scrap scrap = Scrap.from(user);
        scrapRepository.save(scrap);
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 스크랩 폴더인가
     */
    private Scrap getScrap(Long scrapId, User user) {
        return scrapRepository.findByIdAndUser(scrapId, user)
                .orElseThrow(() -> new ScrapException(ScrapErrorResult.SCRAP_NOT_FOUND));
    }

}