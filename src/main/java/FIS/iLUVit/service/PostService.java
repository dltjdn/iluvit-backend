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

        return getPostResponses(posts);
    }

    /**
     * [모두의 이야기 + 유저가 속한 센터의 이야기] 에서  게시글 제목+내용 검색
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

        return getPostResponses(posts);
    }

    /**
     * [시설 이야기] or [모두의 이야기] 에서 게시글 제목+내용 검색
     */
    public Slice<PostResponse> searchPostByCenter(Long userId, Long centerId, String keyword, Pageable pageable) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 시설 이야기일 때 유저가 그 시설과 관계되어있는지 검증하는 로직
        if (centerId != null) {

            Center center = centerRepository.findById(centerId)
                    .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

            if (user.getAuth() == Auth.PARENT) { // 학부모일 때

                //부모의 아이들이 속해있는 센터 리스트에 해당 센터가 있는지 확인
                boolean hasAccess = childRepository.findByParent((Parent)user).stream()
                        .map(Child::getCenter)
                        .filter(Objects::nonNull)
                        .anyMatch(center::equals);

                if (!hasAccess) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }

            } else { // 선생님일 때
                Teacher teacher = teacherRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

                if (!teacher.getCenter().equals(center)) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            }
        }

        // 시설 id not null -> 시설이야기 안에서 검색, 시설 id null -> 모두의 이야기 안에서 검색
        Slice<Post> posts = postRepository.findByCenterAndKeyword(centerId, keyword, pageable);

        return getPostResponses(posts);
    }

    /**
     * 각 게시판 별 게시글 제목+내용 검색
     */
    public Slice<PostResponse> searchByBoard(Long boardId, String input, Pageable pageable) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        Slice<Post> posts = postRepository.findByBoardAndKeyword(boardId, input, pageable);

        return getPostResponses(posts);
    }

    /**
     * HOT 게시판 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByHeartCnt(Long centerId, Pageable pageable) {

        // heartCnt 가 10개 이상이면 HOT 게시판에 넣어줍니다.
        Slice<Post> posts = postRepository.findHotPosts(centerId, Criteria.HOT_POST_HEART_CNT, pageable);

        return getPostResponses(posts);
    }

    /**
     *  게시글 상세 조회
     */
    public PostDetailResponse findPostByPostId(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        List<String> infoImages = imageService.getInfoImages(post);
        String profileImage = imageService.getProfileImage(post.getUser());

        return new PostDetailResponse(post, infoImages, profileImage, userId);
    }

    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByPublic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenterIsNull(user);

        List<BoardPreviewDto> BoardPreviewWithHotBoard = addHotBoardToBoardPreviewDto(null);

        return addBookmarkBoardToBoardPreviewDto(BoardPreviewWithHotBoard,bookmarkList);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByCenter(Long userId, Long centerId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 학부모 일 경우 아이들의 시설 id와 주어진 시설 id중 일치하는 것이 하나라도 있어야 함
        if (user.getAuth() == Auth.PARENT && user instanceof Parent) {
            List<Child> children = childRepository.findByParent((Parent) user);

            boolean flag = children.stream()
                    .filter(c -> c.getCenter() != null && c.getApproval() == Approval.ACCEPT)
                    .map(Child::getCenter)
                    .anyMatch(center -> center.getId().equals(centerId));

            if (!flag) {
                throw new PostException(PostErrorResult.WAITING_OR_REJECT_CANNOT_ACCESS);
            }
        }
        // 선생 일 경우 속한 시설 id 와 주어진 시설 id가 일치해야 함
        else {
            Teacher teacher = teacherRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

            Center center = teacher.getCenter();

            if (center == null || !center.equals(center)) {
                throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }

        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 유저의 시설 즐겨찾기 리스트 가져옴
        List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenter(user, center);

        List<BoardPreviewDto> boardPreviewWithHotBoard = addHotBoardToBoardPreviewDto(center);

        return addBookmarkBoardToBoardPreviewDto(boardPreviewWithHotBoard, bookmarkList);
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
        return posts.map(post -> {
            String previewImage = imageService.getInfoImages(post.getInfoImagePath()).get(0);
            return new PostResponse(post, previewImage);
        });
    }

    /**
     * BoardPreviewDto 리스트에 핫게시판 정보를 추가한다
     */
    private List<BoardPreviewDto> addHotBoardToBoardPreviewDto(Center center){
        //  센터가 null이면 모든 게시물, 센터가 null이 아니면 해당 센터의 게시물 중 핫 게시물을 조회
        List<Post> hotPosts = postRepository.findHotPostsByHeartCnt(Criteria.HOT_POST_HEART_CNT, center, PageRequest.of(0, 3));

        List<BoardPreviewDto> boardPreviewDtos = new ArrayList<>();

        // 핫 게시물 정보를 활용하여 BoardPreviewDto.PostInfo 생성
        List<BoardPreviewDto.PostInfo> postInfoList = hotPosts.stream()
                .map((post) -> new BoardPreviewDto.PostInfo(post, imageService.getInfoImages(post)))
                .collect(Collectors.toList());

        // 핫 게시판 정보를 boardPreviewDtos에 추가
        boardPreviewDtos.add(new BoardPreviewDto(null, "HOT 게시판", postInfoList, BoardKind.NORMAL));

        return boardPreviewDtos;
    }

    /**
     * 기존의 BoardPreviewDto 리스트에 북마크만 게시판 정보들을 추가한다
     */
    @NotNull
    private List<BoardPreviewDto> addBookmarkBoardToBoardPreviewDto(List<BoardPreviewDto> boardPreviewDtos, List<Bookmark> bookmarkList) {
        // 북마크한 게시판을 추출
        List<Board> boards = bookmarkList.stream()
                .map(Bookmark::getBoard)
                .collect(Collectors.toList());

        // 추출한 게시판들의 게시물을 조회
        List<Post> posts = postRepository.findByBoardIn(boards);

        // 게시판별로 게시물들을 그룹화하여 매핑
        Map<Board, List<Post>> boardPostMap = posts.stream()
                .collect(Collectors.groupingBy(Post::getBoard));

        // 게시판별 게시물 정보를 활용하여 BoardPreviewDto 생성
        List<BoardPreviewDto> boardPreviews = boardPostMap.entrySet().stream()
                .map(entry -> {
                    Board board = entry.getKey();
                    List<Post> postList = entry.getValue();

                    List<BoardPreviewDto.PostInfo> postInfos = postList.stream()
                            .map(post -> new BoardPreviewDto.PostInfo(post, imageService.getInfoImages(post)))
                            .collect(Collectors.toList());

                    return new BoardPreviewDto(board.getId(), board.getName(), postInfos, board.getBoardKind());
                })
                .sorted(Comparator.comparing(BoardPreviewDto::getBoardId))
                .collect(Collectors.toList());

        // 기존의 boardPreviewDtos에 북마크 게시판 정보 추가
        boardPreviewDtos.addAll(boardPreviews);

        return boardPreviewDtos;
    }



}
