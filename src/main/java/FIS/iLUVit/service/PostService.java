package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final BoardRepository boardRepository;
    private final CenterRepository centerRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ScrapPostRepository scrapPostRepository;

    public void savePost(PostRegisterRequest request, List<MultipartFile> images, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Integer imgSize = (images == null ? 0 : images.size());

        Board findBoard = boardRepository.findById(request.getBoard_id())
                .orElseThrow(() -> new BoardException("존재하지 않는 보드"));

        if (findBoard.getBoardKind() == BoardKind.NOTICE) {
            if (findUser.getAuth() == Auth.PARENT) {
                throw new PostException("공지 게시판은 교사만 글을 등록할 수 있습니다.");
            }
        }

        Post post = new Post(request.getTitle(), request.getContent(), request.getAnonymous(),
                0, 0, imgSize, 0, findBoard, findUser);

        Post savedPost = postRepository.save(post); // 게시글 저장 -> Id 생김

        if (imgSize > 0) {
            String imagePath = imageService.getPostDir(savedPost.getId()); // id로 경로얻어서 이미지 저장
            imageService.saveInfoImage(images, imagePath);
        }

    }

    public void deleteById(Long postId, Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글"));
        if (!Objects.equals(findPost.getUser().getId(), findUser.getId())) {
            throw new UserException("삭제 권한이 없는 유저");
        }
        postRepository.deleteById(postId);
    }


    public GetPostResponse findById(Long postId) {

        Post findPost = postRepository.findByIdWithUserAndBoardAndCenter(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글"));
        return getPostResponseDto(findPost);
    }

    public Slice<GetPostResponsePreview> searchByKeyword(String input, Auth auth, Long userId, Pageable pageable) {
        log.info("input : " + input);

        Set<Long> centerIds = new HashSet<>();
//        List<Long> boardIds;

        if (auth == Auth.PARENT) {
            // 학부모 유저일 때 아이와 연관된 센터의 아이디를 모두 가져옴
            centerIds = userRepository.findChildren(userId)
                    .stream().filter(c -> c.getCenter() != null).map(c -> c.getCenter().getId())
                    .collect(Collectors.toSet());


        } else {
            Center center = centerRepository.findCenterByTeacher(userId).get();
            centerIds.add(center.getId());

        }

        Slice<GetPostResponsePreview> posts = postRepository.findWithBoardAndCenter(centerIds, input, pageable);
        // 센터의 게시판 + 모두의 게시판(centerId == null) 키워드 검색
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public Slice<GetPostResponsePreview> searchByKeywordAndCenter(Long centerId, String input, Auth auth, Long userId, Pageable pageable) {
        if (centerId == null) {
            return searchByKeyword(input, auth, userId, pageable);
        }
        Slice<GetPostResponsePreview> posts = postRepository.findWithCenter(centerId, input, auth, userId, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public Slice<GetPostResponsePreview> searchByKeywordAndBoard(Long boardId, String input, Pageable pageable) {
        Slice<GetPostResponsePreview> posts = postRepository.findWithBoard(boardId, input, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public GetPostResponse getPostResponseDto(Post post) {
        String postDir = imageService.getPostDir(post.getId());
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(postDir, post.getImgCnt());
        String userProfileDir = imageService.getUserProfileDir();
        String encodedProfileImage = imageService.getEncodedProfileImage(userProfileDir, post.getUser().getId());
        return new GetPostResponse(post, encodedInfoImage, encodedProfileImage);
    }

    public void setPreviewImage(GetPostResponsePreview preview) {
        String postDir = imageService.getPostDir(preview.getPost_id());
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(postDir, preview.getImgCnt());
        preview.updatePreviewImage(encodedInfoImage);
    }

    public PostList searchByUser(Long userId, Pageable pageable) {
        Slice<Post> posts = postRepository.findByUser(userId, pageable);
        Slice<GetPostResponsePreview> preview = posts.map(p -> new GetPostResponsePreview(p));
        return new PostList(preview);
    }

    public List<BoardPreview> searchMainPreview(Long userId) {
        List<BoardPreview> boardPreviews = new ArrayList<>();
        List<Bookmark> bookmarkList = bookmarkRepository.findBoardByUser(userId);
        getBoardPreviews(bookmarkList, boardPreviews);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findByHeartCnt(2, PageRequest.of(0, 4));
        List<BoardPreview> results = new ArrayList<>();

        return getPreivewResult(hotPosts, results, boardPreviews);
    }


    public List<BoardPreview> searchCenterMainPreview(Long userId, Long centerId) {
        List<BoardPreview> boardPreviews = new ArrayList<>();
        List<Bookmark> bookmarkList = bookmarkRepository.findBoardByUserAndCenter(userId, centerId);
        getBoardPreviews(bookmarkList, boardPreviews);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findByHeartCntWithCenter(2, centerId, PageRequest.of(0, 4));
        List<BoardPreview> results = new ArrayList<>();

        return getPreivewResult(hotPosts, results, boardPreviews);
    }

    private void getBoardPreviews(List<Bookmark> bookmarkList, List<BoardPreview> boardPreviews) {
        List<Long> boardIds = bookmarkList.stream()
                .map(bm -> bm.getBoard().getId())
                .collect(Collectors.toList());

        List<Post> top4 = postRepository.findTop4(boardIds);
        Map<Board, List<Post>> boardPostMap = top4.stream()
                .collect(Collectors.groupingBy(Post::getBoard));


        boardPostMap.forEach((k, v) -> {

            List<BoardPreview.PostInfo> postInfos = v.stream()
                    .map(p -> {
                        BoardPreview.PostInfo postInfo = new BoardPreview.PostInfo(p);
                        String postDir = imageService.getPostDir(p.getId());
                        List<String> images = imageService.getEncodedInfoImage(postDir, p.getImgCnt());
                        postInfo.setImages(images);
                        return postInfo;
                    })
                    .collect(Collectors.toList());

            boardPreviews.add(new BoardPreview(k.getId(), k.getName(), postInfos, k.getBoardKind()));
        });
    }

    @NotNull
    private List<BoardPreview> getPreivewResult(List<Post> hotPosts, List<BoardPreview> results, List<BoardPreview> boardPreviews) {
        List<BoardPreview.PostInfo> postInfoList = hotPosts.stream()
                .map(BoardPreview.PostInfo::new)
                .collect(Collectors.toList());

        results.add(new BoardPreview(null, "HOT 게시판", postInfoList, BoardKind.NORMAL));

        boardPreviews = boardPreviews.stream()
                .sorted(Comparator.comparing(BoardPreview::getBoard_id)).collect(Collectors.toList());
        results.addAll(boardPreviews);

        return results;
    }

    public void updateDate(Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글"));
        findPost.updateTime(LocalDateTime.now());
    }

    public Slice<GetPostResponsePreview> findByHeartCnt(Long centerId, Pageable pageable) {
        return postRepository.findHotPosts(centerId, pageable);
    }

    /**
     *   작성날짜: 2022/06/22 4:54 PM
     *   작성자: 이승범
     *   작성내용: 해당 스크랩 폴더의 게시물들 preview 보여주기
     */
    public Slice<GetScrapPostResponsePreview> searchByScrap(Long userId, Long scrapId, Pageable pageable) {
        Slice<ScrapPost> scrapPosts = scrapPostRepository.findByScrapWithPost(userId, scrapId, pageable);
        return scrapPosts.map(GetScrapPostResponsePreview::new);
    }
}
