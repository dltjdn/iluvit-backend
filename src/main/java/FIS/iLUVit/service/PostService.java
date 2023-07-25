package FIS.iLUVit.service;

import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.dto.post.PostCreateRequest;
import FIS.iLUVit.dto.post.PostDetailResponse;
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
    public Long saveNewPost(PostCreateRequest request, Long userId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Board findBoard = boardRepository.findById(request.getBoardId())
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


    /**
     *  게시글 삭제
     */
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
        List<Long> commentIds = commentRepository.findByPost(findPost).stream()
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

    /**
     * 내가 쓴 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByUser(Long userId, Pageable pageable) {
        Slice<Post> posts = postRepository.findByUser(userId, pageable);
        Slice<PostResponse> preview = posts.map(post -> new PostResponse(post));
        return preview;
    }

    /**
     * 장터글 끌어올리기
     */
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

    /**
     * 게시글 제목+내용 검색 ( [모두의 이야기 + 유저가 속한 센터의 이야기] 에서 통합 검색 )
     */
    public Slice<PostResponse> searchPost(String input, Long userId, Pageable pageable) {

        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        Auth auth = findUser.getAuth();

        Set<Long> centerIds = new HashSet<>();

        if (auth == Auth.PARENT) {
            // 학부모 유저일 때 아이와 연관된 센터의 아이디를 모두 가져옴
            centerIds = childRepository.findByParent( (Parent)findUser)
                    .stream().filter(c -> c.getCenter() != null).map(c -> c.getCenter().getId())
                    .collect(Collectors.toSet());
        } else {
            // 교사 유저는 연관된 센터 가져옴
            Center center = ((Teacher)findUser).getCenter();
            if (center != null)
                centerIds.add(center.getId());

        }

        // 센터의 게시판 + 모두의 게시판(centerId == null) 키워드 검색
        Slice<PostResponse> posts = postRepository.findInCenterByKeyword(centerIds, input, pageable);
        // 끌어온 게시글에 이미지 있으면 프리뷰용 이미지 넣어줌
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    /**
     * 게시글 제목+내용+시설 검색 (각 시설 별 검색)
     */
    public Slice<PostResponse> searchPostByCenter(Long centerId, String input, Auth auth, Long userId, Pageable pageable) {
        if (centerId != null) {
            if (auth == Auth.PARENT) {
                // 학부모 유저일 때 아이와 연관된 센터의 아이디를 모두 가져옴
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));
                Set<Long> centerIds = childRepository.findByParent((Parent)user)
                        .stream().filter(c -> c.getCenter() != null).map(c -> c.getCenter().getId())
                        .collect(Collectors.toSet());
                if (!centerIds.contains(centerId)) {
                    log.warn("Set {} 에 센터 아이디가 없음", centerIds);
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            } else {
                Teacher teacher = teacherRepository.findById(userId)
                        .orElseThrow(() -> new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS));
                // 교사 아이디 + center로 join fetch 조회한 결과가 없으면 Teacher의 Center가 null이므로 권한 X
                if (!Objects.equals(teacher.getCenter().getId(), centerId)) {
                    throw new PostException(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
                }
            }
        }
        // 센터 아이디 null 인 경우 모두의 이야기 안에서 검색됨
        Slice<PostResponse> posts = postRepository.findByCenterAndKeyword(centerId, input, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }

    /**
     * 게시글 제목+내용+보드 검색 (각 게시판 별 검색)
     */
    public Slice<PostResponse> searchByBoard(Long boardId, String input, Pageable pageable) {
        Slice<PostResponse> posts = postRepository.findByBoardAndKeyword(boardId, input, pageable);
        posts.forEach(g -> setPreviewImage(g));
        return posts;
    }


    /**
     * 모두의 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByPublic(Long userId) {
        List<BoardPreviewDto> boardPreviews = new ArrayList<>();

        // 비회원일 때 기본 게시판들의 id를 북마크처럼 디폴트로 제공, 회원일 땐 북마크를 통해서 제공
        if (userId == null) {
            List<Board> boardList = boardRepository.findByCenterIsNullAndIsDefaultTrue();
            addBoardPreviews(boardPreviews, boardList);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(()-> new UserException(UserErrorResult.USER_NOT_EXIST));
            List<Bookmark> bookmarkList = boardBookmarkRepository.findByUserAndBoardCenterIsNull(user);
            getBoardPreviews(bookmarkList, boardPreviews);
        }

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findTop3ByHeartCnt(Criteria.HOT_POST_HEART_CNT, PageRequest.of(0, 3));
        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }

    /**
     * 시설별 이야기 게시판 전체 조회
     */
    public List<BoardPreviewDto> findBoardDetailsByCenter(Long userId, Long centerId) {
        if (userId == null) {
            throw new UserException(UserErrorResult.NOT_VALID_TOKEN);
        }

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
        getBoardPreviews(bookmarkList, boardPreviews);

        // HOT 게시판 정보 추가
        List<Post> hotPosts = postRepository.findTop3ByHeartCntWithCenter(Criteria.HOT_POST_HEART_CNT, centerId, PageRequest.of(0, 3));
        List<BoardPreviewDto> results = new ArrayList<>();

        return getPreviewResult(hotPosts, results, boardPreviews);
    }

    /**
     * HOT 게시판 게시글 전체 조회
     */
    public Slice<PostResponse> findPostByHeartCnt(Long centerId, Pageable pageable) {
        // heartCnt 가 n 개 이상이면 HOT 게시판에 넣어줍니다.
        return postRepository.findHotPosts(centerId, Criteria.HOT_POST_HEART_CNT, pageable);
    }

    public PostDetailResponse getPostResponseDto(Post post, Long userId) {
        return new PostDetailResponse(post, imageService.getInfoImages(post), imageService.getProfileImage(post.getUser()), userId);
    }

    public void setPreviewImage(PostResponse preview) {
        List<String> infoImages = imageService.getInfoImages(preview.getPreviewImage());
        preview.updatePreviewImage(infoImages);
    }


    /**
     *  게시글 상세 조회
     */
    public PostDetailResponse findPostByPostId(Long userId, Long postId) {
        // 게시글과 연관된 유저, 게시판, 시설 한 번에 끌고옴
        Post findPost = postRepository.findByIdWithUserAndBoardAndCenter(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_EXIST));

        // 첨부된 이미지 파일, 게시글에 달린 댓글 지연 로딩으로 가져와 DTO 생성
        return getPostResponseDto(findPost, userId);
    }


    private void getBoardPreviews(List<Bookmark> bookmarkList, List<BoardPreviewDto> boardPreviews) {
        List<Board> boardList = bookmarkList.stream()
                .map(bookmark -> bookmark.getBoard())
                .collect(Collectors.toList());

        addBoardPreviews(boardPreviews, boardList);
    }

    private void addBoardPreviews(List<BoardPreviewDto> boardPreviews, List<Board> boardList) {
        List<Long> boardIds = boardList
                .stream().map(Board::getId)
                .collect(Collectors.toList());

        List<Post> top4 = postRepository.findTop3(boardIds);
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
                .sorted(Comparator.comparing(BoardPreviewDto::getBoardId)).collect(Collectors.toList());
        results.addAll(boardPreviews);

        return results;
    }



}
