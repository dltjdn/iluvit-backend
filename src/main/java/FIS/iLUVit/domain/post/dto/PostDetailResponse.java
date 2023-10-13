package FIS.iLUVit.domain.post.dto;

import FIS.iLUVit.domain.comment.dto.CommentInPostResponse;
import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id; // 게시글 아이디

    @JsonProperty("writer_id")
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
    private List<CommentInPostResponse> comments;


    public PostDetailResponse(Post post, List<String> infoImages, String profileImage, Long userId, List<CommentInPostResponse> commentInPostResponses) {
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

        this.comments = commentInPostResponses;
        this.commentCnt = post.getCommentCnt();

    }
}