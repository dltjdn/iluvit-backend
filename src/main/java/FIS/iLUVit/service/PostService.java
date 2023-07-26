package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostCreateRequest;
import FIS.iLUVit.dto.post.PostDetailResponse;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.constant.Criteria;
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
    private final TeacherRepository teacherRepository;
    private final ChildRepository childRepository;
    private final ImageService imageService;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final CommentRepository commentRepository;
    private final CenterRepository centerRepository;
    private final ParentRepository parentRepository;


    /**
     * 게시글 저장
     */
    public void saveNewPost(Long userId, PostCreateRequest postCreateRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Board board = boardRepository.findById(postCreateRequest.getBoardId())
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        // 학부모는 공지 게시판에 게시글 쓸 수 없다
        if (board.getBoardKind() == BoardKind.NOTICE && user.getAuth() == Auth.PARENT ) {
                throw new PostException(PostErrorResult.PARENT_NOT_ACCESS_NOTICE);
        }

        List<MultipartFile> images = postCreateRequest.getImages();

        Post post = new Post(postCreateRequest.getTitle(), postCreateRequest.getContent(), postCreateRequest.getAnonymous(),
                0, 0, 0, images.size(), 0, board, user);

        postRepository.save(post);

        imageService.saveInfoImages(images, post);

    }

    /**
     *  게시글 삭제
     */
    public void deletePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        // 게시글을 쓴 사람만 삭제할 수 있다
        if (!post.getUser().equals(user)) {
            throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // 게시글과 연관된 모든 채팅방의 post_id를 null
        chatRoomRepository.setPostIsNull(postId);

        // 게시글과 연관된 모든 알람의 post_id를 null
        alarmRepository.setPostIsNull(postId);

        // 게시글과 연관된 모든 신고내역의 target_id 를 null
        reportRepository.setTargetIsNullAndStatusIsDelete(postId);

        // 게시글과 연관된 모든 신고상세내역의 target_post_id(fk) 를 null
        reportDetailRepository.setPostIsNull(postId);

        List<Long> commentIds = commentRepository.findByPost(post).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 만약 게시글에 달린 댓글도 신고된 상태라면 해당 댓글의 신고내역의 target_id 를 null
        reportRepository.setTargetIsNullAndStatusIsDelete(commentIds);

        // 만약 게시글에 달린 댓글도 신고된 상태라면 해당 댓글의 신고상세내역의 target_comment_id 를 null
        reportDetailRepository.setCommentIsNull(commentIds);

        postRepository.delete(post);
    }

    /**
     * 내가 쓴 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Slice<Post> posts = postRepository.findByUser(user, pageable);

        Slice<PostResponse> postResponses = getPostResponses(posts);

        return postResponses;
    }

    /**
     * 게시글 제목+내용 검색 ( [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 통합 검색 )
     */
    public Slice<PostResponse> searchPost(Long userId, String keyword, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Center> centers = new ArrayList<>();

        if (user.getAuth() == Auth.PARENT) {

            // 학부모 유저일 때 아이와 연관된 센터를 모두 가져옴
            centers = childRepository.findByParent((Parent)user).stream()
                    .map(Child::getCenter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
            Center center = teacher.getCenter();

            if (center != null) centers.add(center);
        }

        // 센터의 게시판 + 모두의 게시판(centerId == null) 키워드 검색
        Slice<Post> posts = postRepository.findInCenterByKeyword(centers, keyword, pageable);

        Slice<PostResponse> postResponses = getPostResponses(posts);

        return postResponses;
    }
    /**
     * 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    public Slice<PostResponse> searchPostByCenter(Long userId, Long centerId, String keyword, Pageable pageable) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        if (centerId != null) {

            Center center = centerRepository.findById(centerId)
                    .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

            if (user.getAuth() == Auth.PARENT) {

                //부모의 아이들이 속해있는 센터 리스트에 해당 센터가 있는지 확인
                boolean hasAccess = childRepository.findByParent((Parent)user).stream()
                        .map(Child::getCenter)
                        .filter(Objects::nonNull)
                        .anyMatch(center::equals);

                if (!hasAccess) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }

            } else {
                Teacher teacher = teacherRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

                if (!teacher.getCenter().equals(center)) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            }
        }
        // 센터 아이디 null 인 경우 모두의 이야기 안에서 검색됨
        Slice<Post> posts = postRepository.findByCenterAndKeyword(centerId, keyword, pageable);

        Slice<PostResponse> postResponses = getPostResponses(posts);

        return postResponses;
    }

    /**
     * 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    public Slice<PostResponse> searchByBoard(Long boardId, String input, Pageable pageable) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        Slice<Post> posts = postRepository.findByBoardAndKeyword(boardId, input, pageable);

        Slice<PostResponse> postResponses = getPostResponses(posts);

        return postResponses;
    }

    /**
     * HOT 게시판 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByHeartCnt(Long centerId, Pageable pageable) {

        // heartCnt 가 10개 이상이면 HOT 게시판에 넣어줍니다.
        Slice<Post> posts = postRepository.findHotPosts(centerId, Criteria.HOT_POST_HEART_CNT, pageable);

        Slice<PostResponse> postResponses = getPostResponses(posts);

        return postResponses;
    }

    /**
     *  게시글 상세 조회
     */
    public PostDetailResponse findPostByPostId(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        List<String> infoImages = imageService.getInfoImages(post);
        String profileImage = imageService.getProfileImage(post.getUser());

        PostDetailResponse postDetailResponse = new PostDetailResponse(post, infoImages, profileImage, userId);

        return postDetailResponse;
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByPublic(Long userId) {
        List<BoardPreviewDto> boardPreviews = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenterIsNull(user);

        List<Board> boardList = bookmarkList.stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList());

        addBoardPreviews(boardPreviews, boardList);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findHotPostsByHeartCnt(Criteria.HOT_POST_HEART_CNT,null, PageRequest.of(0, 3));
        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByCenter(Long userId, Long centerId) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        if (findUser.getAuth() == Auth.PARENT && findUser instanceof Parent) { // 학부모 유저일 경우 아이를 통해 센터 정보를 가져옴
            Parent parent = (Parent) findUser;
            boolean flag = parent.getChildren().stream()
                    .filter(c -> c.getCenter() != null && c.getApproval() == Approval.ACCEPT)
                    .map(Child::getCenter)
                    .anyMatch(center -> center.getId().equals(centerId));

            if (!flag) {
                throw new PostException(PostErrorResult.WAITING_OR_REJECT_CANNOT_ACCESS);
            }

        } else {   // 교사 유저일 경우 바로 센터 정보 가져옴
            Teacher teacher = (Teacher) findUser;
            Center center = teacher.getCenter();

            if (center == null || !center.getId().equals(centerId)) {
                throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }

        List<BoardPreviewDto> boardPreviews = new ArrayList<>();
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));
        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenter(findUser, center);

        List<Board> boardList = bookmarkList.stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList());

        addBoardPreviews(boardPreviews, boardList);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findHotPostsByHeartCnt(Criteria.HOT_POST_HEART_CNT, centerId, PageRequest.of(0, 3));
        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }



    /**
     * 장터글 끌어올리기
     */
    public void pullUpPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        if (post.getUser().getId().equals(userId)) {
            throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // 장터글 끌어올리기 (postUpdateDate 현재시간으로 업데이트)
        post.updateTime(LocalDateTime.now());
    }

    /**
     * Post -> PostResposne
     */
    @NotNull
    private Slice<PostResponse> getPostResponses(Slice<Post> posts) {
        Slice<PostResponse> postResponses = posts.map(post -> {
            String previewImage = imageService.getInfoImages(post.getInfoImagePath()).get(0);
            return new PostResponse(post, previewImage);
        });
        return postResponses;
    }

    private void addBoardPreviews(List<BoardPreviewDto> boardPreviews, List<Board> boards) {

        List<Post> top3Posts = postRepository.findFirst3ByBoardInOrderByBoardIdAscCreatedDateDesc(boards);
        Map<Board, List<Post>> boardPostMap = top3Posts.stream()
                .collect(Collectors.groupingBy(post -> post.getBoard()));

        for (Board board : boards) {
            if (!boardPostMap.containsKey(board)) {
                boardPostMap.put(board, new ArrayList<>());
            }
        }

        boardPostMap.forEach((board, postList) -> {
            List<BoardPreviewDto.PostInfo> postInfos = postList.stream()
                    .map(post -> {
                        BoardPreviewDto.PostInfo postInfo = new BoardPreviewDto.PostInfo(post);
                        postInfo.addImagesInPostInfo(imageService.getInfoImages(post));
                        return postInfo;
                    })
                    .collect(Collectors.toList());

            boardPreviews.add(new BoardPreviewDto(board.getId(), board.getName(), postInfos, board.getBoardKind()));
        });
    }

    @NotNull
    private List<BoardPreviewDto> getPreviewResult(List<Post> hotPosts, List<BoardPreviewDto> results, List<BoardPreviewDto> boardPreviews) {
        List<BoardPreviewDto.PostInfo> postInfoList = hotPosts.stream()
                .map((Post post) -> {
                    BoardPreviewDto.PostInfo postInfo = new BoardPreviewDto.PostInfo(post);
                    postInfo.addImagesInPostInfo(imageService.getInfoImages(post));
                    return postInfo;
                })
                .collect(Collectors.toList());

        results.add(new BoardPreviewDto(null, "HOT 게시판", postInfoList, BoardKind.NORMAL));

        boardPreviews = boardPreviews.stream()
                .sorted(Comparator.comparing(BoardPreviewDto::getBoardId)).collect(Collectors.toList());
        results.addAll(boardPreviews);

        return results;
    }



}
