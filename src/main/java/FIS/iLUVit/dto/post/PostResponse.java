package FIS.iLUVit.dto.post;

import FIS.iLUVit.dto.comment.CommentResponse;
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
public class PostResponse {

    private Long id; // 게시글 아이디
    private Long writer_id;
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

    private List<CommentResponse> comments;
    private Integer commentCnt;

    private Long boardId;
    private Long centerId;

    private Boolean canDelete;

    public PostResponse(Post post, List<String> infoImages, String profileImage,Long userId, List<CommentResponse> commentResponses) {
        this.id = post.getId();
        if (post.getUser() != null) {
            this.writer_id = post.getUser().getId();
            if (Objects.equals(post.getUser().getId(), userId)) {
                this.canDelete = true;
            } else {
                this.canDelete = false;
            }
            if (post.getAnonymous()) {
                this.nickname = "익명";
            } else {
                this.nickname = post.getUser().getNickName();
            }
        }
        this.anonymous = post.getAnonymous();
        this.date = post.getDate();
        this.time = post.getTime();
        this.title = post.getTitle();
        this.content = post.getContent();

        this.profileImage = profileImage;
        this.images = infoImages;

        this.imgCnt = infoImages.size();
        this.heartCnt = post.getHeartCnt();

        if (post.getBoard() != null) {
            this.boardId = post.getBoard().getId();
            this.boardName = post.getBoard().getName();
            if (post.getBoard().getCenter() != null) {
                this.centerId = post.getBoard().getCenter().getId();
                this.centerName = post.getBoard().getCenter().getName();
            }
        }

        this.comments = commentResponses;
        this.commentCnt = post.getCommentCnt();

    }
}
