package FIS.iLUVit.service;

import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.comment.CommentResponse;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.dto.post.PostRequest;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.constant.Criteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailRepository reportDetailRepository;
    private final CommentRepository commentRepository;
    private final BlockedRepository blockedRepository;

//    private final Integer heartCriteria = 2; // HOT 게시판 좋아요 기준

    public Long saveNewPost(PostRequest request, Long userId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Board findBoard = boardRepository.findById(request.getBoard_id())
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        if (findBoard.getBoardKind() == BoardKind.NOTICE) {
            if (findUser.getAuth() == Auth.PARENT) {
                throw new PostException(PostErrorResult.PARENT_NOT_ACCESS_NOTICE);
            }
        }
        List<MultipartFile> images = request.getImages();
        Integer imgSize = (images == null ? 0 : images.size());
        Post post = new Post(request.getTitle(), request.getContent(), request.getAnonymous(),
                0, 0, 0, imgSize, 0, findBoard, findUser);
        Post savedPost = postRepository.save(post); // 게시글 저장 -> Id 생김
        imageService.saveInfoImages(images, post);

        return savedPost.getId();

    }

    public Long deletePost(Long postId, Long userId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        List<Long> postIds = List.of(postId);
        // 게시글과 연관된 모든 채팅방의 post_id(fk) 를 null 값으로 만들어줘야함.
        chatRoomRepository.setPostIsNull(postIds);
        // 게시글과 연관된 모든 알람의 post_id(fk) 를 null 값으로 만들어줘야함.
        alarmRepository.setPostIsNull(postId);

        // 2022-09-20 최민아
        //------------------------신고 관련------------------------//
        // 게시글과 연관된 모든 신고내역의 target_id 를 null 값으로 만들어줘야함.
        reportRepository.setTargetIsNullAndStatusIsDelete(postId);
        // 게시글과 연관된 모든 신고상세내역의 target_post_id(fk) 를 null 값으로 만들어줘야함.
        reportDetailRepository.setPostIsNull(postId);

        //------------------------댓글 관련------------------------//
        List<Long> commentIds = commentRepository.findByPostId(postId).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        // 만약 게시글에 달린 댓글도 신고된 상태라면 해당 댓글의 신고내역의 target_id 를 null 값으로 만들어줘야함.
        reportRepository.setTargetIsNullAndStatusIsDelete(commentIds);
        // 만약 게시글에 달린 댓글도 신고된 상태라면 해당 댓글의 신고상세내역의 target_comment_id 를 null 값으로 만들어줘야함.
        reportDetailRepository.setCommentIsNull(commentIds);

        if (!Objects.equals(findPost.getUser().getId(), findUser.getId())) {
            throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        postRepository.delete(findPost);
        return postId;
    }


    public PostResponse findPostByPostId(Long userId, Long postId) {
        // 게시글과 연관된 유저, 게시판, 시설 한 번에 끌고옴
        Post findPost = postRepository.findByIdWithUserAndBoardAndCenter(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        // 첨부된 이미지 파일, 게시글에 달린 댓글 지연 로딩으로 가져와 DTO 생성
        return getPostResponseDto(findPost, userId);
    }

    // [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 통합 검색
    public Slice<PostPreviewDto> searchPost(String input, Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Auth auth = user.getAuth();

        Set<Long> centerIds = new HashSet<>();

        if (auth == Auth.PARENT) {
            // 학부모 유저일 때 아이와 연관된 센터의 아이디를 모두 가져옴
            centerIds = userRepository.findChildren(userId)
                    .stream().filter(c -> c.getCenter() != null).map(c -> c.getCenter().getId())
                    .collect(Collectors.toSet());
        } else {
            // 교사 유저는 연관된 센터 가져옴
            Center center = ((Teacher)user).getCenter();
            if (center != null)
                centerIds.add(center.getId());

        }

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        // 센터의 게시판 + 모두의 게시판(centerId == null) 키워드 검색
        Slice<PostPreviewDto> posts = postRepository.findInCenterByKeyword(centerIds, input, blockedUserIds, pageable);
        // 끌어온 게시글에 이미지 있으면 프리뷰용 이미지 넣어줌
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public Slice<PostPreviewDto> searchPostByCenter(Long centerId, String input, Auth auth, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        if (centerId != null) {
            if (auth == Auth.PARENT) {
                // 학부모 유저일 때 아이와 연관된 센터의 아이디를 모두 가져옴
                Set<Long> centerIds = userRepository.findChildren(userId)
                        .stream().filter(c -> c.getCenter() != null).map(c -> c.getCenter().getId())
                        .collect(Collectors.toSet());
                if (!centerIds.contains(centerId)) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            } else {
                Teacher teacher = userRepository.findTeacherById(userId)
                        .orElseThrow(() -> new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS));
                // 교사 아이디 + center로 join fetch 조회한 결과가 없으면 Teacher의 Center가 null이므로 권한 X
                if (!Objects.equals(teacher.getCenter().getId(), centerId)) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            }
        }

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);
        // 센터 아이디 null 인 경우 모두의 이야기 안에서 검색됨
        Slice<PostPreviewDto> posts = postRepository.findByCenterAndKeyword(centerId, input, blockedUserIds, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public Slice<PostPreviewDto> searchByBoard(Long userId, Long boardId, String input, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        Slice<PostPreviewDto> posts = postRepository.findByBoardAndKeyword(boardId, input, blockedUserIds, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    public PostResponse getPostResponseDto(Post post, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);
        List<CommentResponse> commentResponses = new ArrayList<>();

        // 대댓글 포함한 댓글 리스트 조회
        post.getComments().forEach(comment -> {
            // 대댓글 리스트 조회
            List<CommentResponse> subCommentResponses = new ArrayList<>();
            comment.getSubComments().forEach(subComment -> {
                Boolean SubCommentIsBlocked = false;
                Long subCommentUserId = null;
                if(subComment.getUser() != null){
                    subCommentUserId = subComment.getUser().getId();
                }
                if(subCommentUserId != null && blockedUserIds.contains(subCommentUserId)){
                    SubCommentIsBlocked=true;
                }
                subCommentResponses.add(new CommentResponse(subComment, subCommentUserId, SubCommentIsBlocked));
            });

            // 댓글 리스트 조회
            boolean commentIsBlocked = false;
            Long commentUserId = null;
            if(comment.getUser() != null){
                commentUserId = comment.getUser().getId();
            }
            if(commentUserId != null && blockedUserIds.contains(commentUserId)){
                commentIsBlocked = true;
            }
            commentResponses.add(new CommentResponse(comment, commentUserId,subCommentResponses, commentIsBlocked));

        });

        String profileImage = imageService.getProfileImage(post.getUser());
        List<String> infoImages = imageService.getInfoImages(post);
        return new PostResponse(post, infoImages, profileImage, userId, commentResponses);
    }
    public void setPreviewImage(PostPreviewDto preview) {
//        String postDir = imageService.getPostDir(preview.getPost_id());
//        List<String> encodedInfoImage = imageService.getEncodedInfoImage(postDir, preview.getImgCnt());
        List<String> infoImages = imageService.getInfoImages(preview.getPreviewImage());
        preview.updatePreviewImage(infoImages);
    }

    public Slice<PostPreviewDto> findPostByUser(Long userId, Pageable pageable) {
        Slice<Post> posts = postRepository.findByUser(userId, pageable);
        Slice<PostPreviewDto> preview = posts.map(post -> new PostPreviewDto(post));
        return preview;
    }

    public List<BoardPreviewDto> findBoardDetailsByPublic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        List<BoardPreviewDto> boardPreviews = new ArrayList<>();

        List<Bookmark> bookmarkList = boardBookmarkRepository.findBoardByUser(userId);


        getBoardPreviews(bookmarkList, boardPreviews, blockedUserIds);

        // HOT 게시판 정보 추가 ( 유저가 차단한 유저 리스트를 넘겨주어 해당 게시물은 조회되지 않게 한다)
        List<Post> hotPosts = null;
        if(blockedUserIds.size() == 0){
            hotPosts = postRepository.findTop3ByHeartCnt(Criteria.HOT_POST_HEART_CNT);
        }else{
            hotPosts = postRepository.findTop3ByHeartCnt(Criteria.HOT_POST_HEART_CNT, blockedUserIds);
        }

        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }


    public List<BoardPreviewDto> findBoardDetailsByCenter(Long userId, Long centerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        // 학부모 유저일 경우 아이를 통해 센터 정보를 가져옴
        // 교사 유저일 경우 바로 센터 정보 가져옴
        if (user.getAuth() == Auth.PARENT) {
            boolean flag;
            List<Long> centerIds = ((Parent) user).getChildren()
                    .stream()
                    .filter(c -> c.getCenter() != null && c.getApproval() == Approval.ACCEPT)
                    .map(c -> c.getCenter().getId())
                    .collect(Collectors.toList());
            flag = centerIds.stream().anyMatch(id -> Objects.equals(id, centerId));
            if (!flag) {
                throw new PostException(PostErrorResult.WAITING_OR_REJECT_CANNOT_ACCESS);
            }

        } else {
            Center center = ((Teacher) user).getCenter();
            if (center == null || !Objects.equals(center.getId(), centerId)) {
                throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }

        List<BoardPreviewDto> boardPreviews = new ArrayList<>();
        List<Bookmark> bookmarkList = boardBookmarkRepository.findBoardByUserAndCenter(userId, centerId);
        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        getBoardPreviews(bookmarkList, boardPreviews, blockedUserIds);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = null;
        if(blockedUserIds.size() == 0){
            hotPosts = postRepository.findTop3ByHeartCntWithCenter(Criteria.HOT_POST_HEART_CNT, centerId);
        }else{
            hotPosts = postRepository.findTop3ByHeartCntWithCenter(Criteria.HOT_POST_HEART_CNT, centerId, blockedUserIds);
        }

        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }

    private void getBoardPreviews(List<Bookmark> bookmarkList, List<BoardPreviewDto> boardPreviews, List<Long> blockedUserIds) {
        List<Board> boardList = bookmarkList.stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList());

        addBoardPreviews(boardPreviews, boardList, blockedUserIds);
    }

    private void addBoardPreviews(List<BoardPreviewDto> boardPreviews, List<Board> boardList, List<Long> blockedUserIds) {
        List<Long> boardIds = boardList
                .stream().map(Board::getId)
                .collect(Collectors.toList());

        List<Post> top4 = null;
        if( blockedUserIds.size() == 0){
            top4 = postRepository.findTop3(boardIds);
        }else{
            top4 = postRepository.findTop3(boardIds,blockedUserIds);
        }

        Map<Board, List<Post>> boardPostMap = top4.stream()
                .collect(Collectors.groupingBy(post -> post.getBoard()));

        for (Board board : boardList) {
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
                .sorted(Comparator.comparing(BoardPreviewDto::getBoard_id)).collect(Collectors.toList());
        results.addAll(boardPreviews);

        return results;
    }

    public void pullUpPost(Long userId, Long postId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));
        if (!Objects.equals(findPost.getUser().getId(), userId)) {
            throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
        }
        findPost.updateTime(LocalDateTime.now());
    }

    public Slice<PostPreviewDto> findPostByHeartCnt(Long userId, Long centerId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        // heartCnt 가 n 개 이상이면 HOT 게시판에 넣어줍니다.
        return postRepository.findHotPosts(centerId, Criteria.HOT_POST_HEART_CNT, blockedUserIds, pageable);
    }

    private List<Long> getBlockedUserIds(User user) {
        List<Long> blockedUserIds = blockedRepository.findByBlockingUser(user).stream()
                .map(Blocked::getBlockedUser)
                .map(User::getId)
                .collect(Collectors.toList());
        return blockedUserIds;
    }

}
