package FIS.iLUVit.dto.post;

import FIS.iLUVit.dto.comment.CommentDto;
import FIS.iLUVit.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id; // 게시글 아이디
    private Long writerId;
    private String nickname;
    private Boolean anonymous;
    private LocalDate date;
    private LocalTime time;
    private String title;
    @Lob
    private String content;

    private String profileImage;
    private List<String> images;

    private Integer imgCnt;
    private Integer heartCnt;

    private String boardName;
    private String centerName;

    private Long boardId;
    private Long centerId;

    private Boolean canDelete;
    private Integer commentCnt;
    private List<CommentDto> comments;


    public PostDetailResponse(Post post, List<String> encodedImages, String encodedProfileImage, Long userId) {
        this.id = post.getId();
        if (post.getUser() != null) {
            if (Objects.equals(post.getUser().getId(), userId)) {
                this.canDelete = true;
            } else {
                this.canDelete = false;
            }
            if (post.getAnonymous()) {
                this.nickname = "익명";
            } else {
                this.writerId = post.getUser().getId();
                this.nickname = post.getUser().getNickName();
            }
        }
        this.anonymous = post.getAnonymous();
        this.date = post.getDate();
        this.time = post.getTime();
        this.title = post.getTitle();
        this.content = post.getContent();

        this.profileImage = encodedProfileImage;
        this.images = encodedImages;

        this.imgCnt = encodedImages.size();
        this.heartCnt = post.getHeartCnt();

        if (post.getBoard() != null) {
            this.boardId = post.getBoard().getId();
            this.boardName = post.getBoard().getName();
            if (post.getBoard().getCenter() != null) {
                this.centerId = post.getBoard().getCenter().getId();
                this.centerName = post.getBoard().getCenter().getName();
            }
        }

        this.comments = post.getComments().stream()
                .filter(c -> c.getParentComment() == null)
                .map(c -> new CommentDto(c, userId)).collect(Collectors.toList());
        this.commentCnt = post.getCommentCnt();

    }
}
