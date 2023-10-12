package FIS.iLUVit.domain.post.service;

import FIS.iLUVit.domain.alarm.repository.AlarmRepository;
import FIS.iLUVit.domain.blocked.domain.Blocked;
import FIS.iLUVit.domain.blocked.repository.BlockedRepository;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.exception.BoardErrorResult;
import FIS.iLUVit.domain.board.exception.BoardException;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.domain.Bookmark;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.chat.repository.ChatRoomRepository;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.child.repository.ChildRepository;
import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.comment.repository.CommentRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.post.exception.PostErrorResult;
import FIS.iLUVit.domain.post.exception.PostException;
import FIS.iLUVit.domain.post.repository.PostRepository;
import FIS.iLUVit.domain.report.repository.ReportDetailRepository;
import FIS.iLUVit.domain.report.repository.ReportRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.board.domain.BoardKind;
import FIS.iLUVit.domain.board.dto.BoardPreviewResponse;
import FIS.iLUVit.domain.comment.dto.CommentInPostResponse;
import FIS.iLUVit.domain.comment.dto.CommentReplyResponse;
import FIS.iLUVit.domain.post.dto.PostCreateRequest;
import FIS.iLUVit.domain.post.dto.PostDetailResponse;
import FIS.iLUVit.domain.post.dto.PostResponse;
import FIS.iLUVit.domain.common.service.ImageService;
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
    private final BlockedRepository blockedRepository;

    public static final Integer HOT_POST_HEART_CNT = 10; // 핫 게시판에 등록될 게시글 좋아요 개수 기준

    /**
     * 게시글 저장
     */
    public void saveNewPost(Long userId, PostCreateRequest postCreateRequest) {

        User user = getUser(userId);

        Board board = getBoard(postCreateRequest.getBoardId());

        // 학부모는 공지 게시판에 게시글 쓸 수 없다
        if (board.getBoardKind() == BoardKind.NOTICE && user.getAuth() == Auth.PARENT ) {
                throw new PostException(PostErrorResult.PARENT_CANNOT_WRITE_NOTICE);
        }

        List<MultipartFile> images = postCreateRequest.getImages();

        Integer imageSize = images == null? 0 : images.size();

        Post post = new Post(postCreateRequest.getTitle(), postCreateRequest.getContent(), postCreateRequest.getAnonymous(),
                0, 0, 0, imageSize, 0, board, user);

        postRepository.save(post);

        imageService.saveInfoImages(images, post);
    }

    /**
     *  게시글 삭제
     */
    public void deletePost(Long postId, Long userId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        // 게시글을 쓴 사람만 삭제할 수 있다
        if (!post.getUser().equals(user)) {
            throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
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
        User user = getUser(userId);

        Slice<Post> posts = postRepository.findByUser(user, pageable);

        return getPostResponses(posts);
    }

    /**
     * [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 게시글 제목+내용 검색
     */
    public Slice<PostResponse> searchPost(Long userId, String keyword, Pageable pageable) {
        if(keyword == null || keyword == "") throw new PostException(PostErrorResult.MISSING_SEARCH_KEYWORD);

        User user = getUser(userId);

        List<Center> centers = new ArrayList<>();

        if (user.getAuth() == Auth.PARENT) {
            // 학부모 유저일 때 아이와 연관된 센터를 모두 가져옴
            centers = childRepository.findByParent((Parent)user).stream()
                    .map(Child::getCenter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
            Center center = teacher.getCenter();

            if (center != null) centers.add(center);
        }

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        // 센터의 게시판 + 모두의 게시판(centerId == null) 키워드 검색
        Slice<Post> posts = postRepository.findInCenterByKeyword(centers, keyword, blockedUserIds, pageable);

        return getPostResponses(posts);
    }

    /**
     * [시설 이야기] or [모두의 이야기] 에서 게시글 제목+내용 검색
     */
    public Slice<PostResponse> searchPostByCenter(Long userId, Long centerId, String keyword, Pageable pageable) {
        if(keyword == null || keyword == "") throw new PostException(PostErrorResult.MISSING_SEARCH_KEYWORD);

        User user = getUser(userId);

        // 시설 이야기일 때 유저가 그 시설과 관계되어있는지 검증하는 로직
        if (centerId != null) {

            Center center = getCenter(centerId);

            if (user.getAuth() == Auth.PARENT) { // 학부모일 때

                //부모의 아이들이 속해있는 센터 리스트에 해당 센터가 있는지 확인
                boolean hasAccess = childRepository.findByParent((Parent)user).stream()
                        .map(Child::getCenter)
                        .filter(Objects::nonNull)
                        .anyMatch(center::equals);

                if (!hasAccess) {
                    throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
                }

            } else { // 선생님일 때
                Teacher teacher = teacherRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

                if (!teacher.getCenter().equals(center)) {
                    throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
                }
            }
        }

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        // 시설 id not null -> 시설이야기 안에서 검색, 시설 id null -> 모두의 이야기 안에서 검색
        Slice<Post> posts = postRepository.findByCenterAndKeyword(centerId, keyword, blockedUserIds, pageable);

        return getPostResponses(posts);
    }



    /**
     * 각 게시판 별 게시글 제목+내용 검색
     */
    public Slice<PostResponse> searchByBoard(Long userId, Long boardId, String keyword, Pageable pageable) {
        User user = getUser(userId);

        getBoard(boardId);

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        Slice<Post> posts = postRepository.findByBoardAndKeyword(boardId, keyword, blockedUserIds, pageable);

        return getPostResponses(posts);
    }


    /**
     * HOT 게시판 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByHeartCnt(Long userId, Long centerId, Pageable pageable) {
        User user = getUser(userId);
        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        // heartCnt 가 10개 이상이면 HOT 게시판에 넣어줍니다.
        Slice<Post> posts = postRepository.findHotPosts(centerId, HOT_POST_HEART_CNT, blockedUserIds, pageable);

        return getPostResponses(posts);
    }


    /**
     *  게시글 상세 조회
     */
    public PostDetailResponse findPostByPostId(Long userId, Long postId) {
        Post post = getPost(postId);

        User user = getUser(userId);

        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);
        List<CommentInPostResponse> commentResponses = new ArrayList<>();


        // 댓글 리스트 조회
        List<Comment> comments = commentRepository.findByPostAndParentCommentIsNull(post);


        // 대댓글 포함한 댓글 리스트 조회
        comments.forEach(comment -> {
            // 대댓글 Response List 만들기
            List<CommentReplyResponse> subCommentResponses = new ArrayList<>();
            List<Comment> subComments = commentRepository.findByParentComment(comment);

            subComments.forEach(subComment -> {

                Boolean SubCommentIsBlocked = false;
                if(subComment.getUser() != null && blockedUserIds.contains(subComment.getUser().getId())){
                    SubCommentIsBlocked=true;
                }
                subCommentResponses.add(new CommentReplyResponse(subComment, userId, SubCommentIsBlocked));
            });

            // 댓글 Response List 만들기
            boolean commentIsBlocked = false;
            if(comment.getUser() != null && blockedUserIds.contains(comment.getUser().getId())){
                commentIsBlocked = true;
            }
            commentResponses.add(new CommentInPostResponse(comment, userId, subCommentResponses, commentIsBlocked));

        });

        String profileImage = imageService.getProfileImage(post.getUser());
        List<String> infoImages = imageService.getInfoImages(post);

        return new PostDetailResponse(post, infoImages, profileImage, userId, commentResponses);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public List<BoardPreviewResponse> findBoardDetailsByPublic(Long userId) {
        User user = getUser(userId);

        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenterIsNull(user);

        List<BoardPreviewResponse> BoardPreviewWithHotBoard = addHotBoardToBoardPreviewDto(user, null);

        return addBookmarkBoardToBoardPreviewDto(BoardPreviewWithHotBoard,bookmarkList);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    public List<BoardPreviewResponse> findBoardDetailsByCenter(Long userId, Long centerId) {

        User user = getUser(userId);

        // 학부모 일 경우 아이들의 시설 id와 주어진 시설 id중 일치하는 것이 하나라도 있어야 함
        if (user.getAuth() == Auth.PARENT && user instanceof Parent) {
            List<Child> children = childRepository.findByParent((Parent) user);

            boolean flag = children.stream()
                    .filter(c -> c.getCenter() != null && c.getApproval() == Approval.ACCEPT)
                    .map(Child::getCenter)
                    .anyMatch(center -> center.getId().equals(centerId));

            if (!flag) {
                throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
            }
        }
        // 선생 일 경우 속한 시설 id 와 주어진 시설 id가 일치해야 함
        else {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

            Center center = teacher.getCenter();

            if (center == null || !center.equals(center)) {
                throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
            }
        }

        Center center = getCenter(centerId);

        // 유저의 시설 즐겨찾기 리스트 가져옴
        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenter(user, center);

        List<BoardPreviewResponse> boardPreviewWithHotBoard = addHotBoardToBoardPreviewDto(user, center);

        return addBookmarkBoardToBoardPreviewDto(boardPreviewWithHotBoard, bookmarkList);
    }


    /**
     * 장터글 끌어올리기
     */
    public void pullUpPost(Long userId, Long postId) {
        Post post = getPost(postId);

        if (post.getUser().getId().equals(userId)) {
            throw new PostException(PostErrorResult.FORBIDDEN_ACCESS);
        }

        // 장터글 끌어올리기 (postUpdateDate 현재시간으로 업데이트)
        post.updateTime(LocalDateTime.now());
    }


    /**
     * Post -> PostResposne
     */
    @NotNull
    private Slice<PostResponse> getPostResponses(Slice<Post> posts) {
        return posts.map(post -> {

            List<String> infoImages = imageService.getInfoImages(post.getInfoImagePath());
            String previewImage = null;
            if(infoImages.size() != 0) previewImage = infoImages.get(0);
            return new PostResponse(post, previewImage);
        });
    }

    /**
     * BoardPreviewResponse 리스트에 핫게시판 정보를 추가한다
     */
    private List<BoardPreviewResponse> addHotBoardToBoardPreviewDto(User user, Center center){
        // 유저가 차단한 유저를 조회한다
        List<Long> blockedUserIds = getBlockedUserIds(user);

        //  센터가 null이면 모든 게시물, 센터가 null이 아니면 해당 센터의 게시물 중 핫 게시물을 조회
        Long centerId = null;
        if (center != null) centerId = center.getId();
        List<Post> hotPosts = postRepository.findHotPostsByHeartCnt(HOT_POST_HEART_CNT, centerId, blockedUserIds);

        List<BoardPreviewResponse> boardPreviewResponses = new ArrayList<>();

        // 핫 게시물 정보를 활용하여 BoardPreviewResponse.PostInfo 생성
        List<BoardPreviewResponse.PostInfo> postInfoList = hotPosts.stream()
                .map((post) -> new BoardPreviewResponse.PostInfo(post, imageService.getInfoImages(post)))
                .collect(Collectors.toList());

        // 핫 게시판 정보를 boardPreviewDtos에 추가
        boardPreviewResponses.add(new BoardPreviewResponse(null, "HOT 게시판", BoardKind.NORMAL, postInfoList));

        return boardPreviewResponses;
    }

    /**
     * 기존의 BoardPreviewResponse 리스트에 북마크만 게시판 정보들을 추가한다
     */
    @NotNull
    private List<BoardPreviewResponse> addBookmarkBoardToBoardPreviewDto(List<BoardPreviewResponse> boardPreviewResponses, List<Bookmark> bookmarkList) {
        // 북마크한 게시판을 추출
        List<Board> boards = bookmarkList.stream()
                .map(Bookmark::getBoard)
                .collect(Collectors.toList());

        // 추출한 게시판들의 게시물을 조회
        List<Post> posts = postRepository.findByBoardIn(boards);

        // 게시판별로 게시물들을 그룹화하여 매핑
        Map<Board, List<Post>> boardPostMap = posts.stream()
                .collect(Collectors.groupingBy(Post::getBoard));

        // 게시판별 게시물 정보를 활용하여 BoardPreviewResponse 생성
        List<BoardPreviewResponse> boardPreviews = boardPostMap.entrySet().stream()
                .map(entry -> {
                    Board board = entry.getKey();
                    List<Post> postList = entry.getValue();

                    List<BoardPreviewResponse.PostInfo> postInfos = postList.stream()
                            .map(post -> new BoardPreviewResponse.PostInfo(post, imageService.getInfoImages(post)))
                            .collect(Collectors.toList());

                    return new BoardPreviewResponse(board.getId(), board.getName(),board.getBoardKind(),  postInfos);
                })
                .sorted(Comparator.comparing(BoardPreviewResponse::getBoardId))
                .collect(Collectors.toList());

        // 기존의 boardPreviewDtos에 북마크 게시판 정보 추가
        boardPreviewResponses.addAll(boardPreviews);

        return boardPreviewResponses;
    }

    /**
     * 해당 유저가 차단한 유저 id의 리스트를 조회합니다
     */
    private List<Long> getBlockedUserIds(User user) {
        List<Long> blockedUserIds = blockedRepository.findByBlockingUser(user).stream()
                .map(Blocked::getBlockedUser)
                .map(User::getId)
                .collect(Collectors.toList());
        return blockedUserIds;
    }
    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 게시글인가
     */
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));
    }
    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        return  centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 게시판인가
     */
    private Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_FOUND));
    }

}