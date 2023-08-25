package FIS.iLUVit.dto.comment;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private Long writer_id;
    private String nickname;
    private String profileImage;
    private String content;
    private Integer heartCnt;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;
    private Boolean canDelete;
    private Boolean isBlocked;  // 댓글 차단 여부
    private List<CommentResponse> answers;

    public CommentResponse(Comment comment, Long userId, List<CommentResponse> subComments, Boolean isBlocked) {
        this.id = comment.getId();
        User writer = comment.getUser();
        if (writer != null) {
            if (Objects.equals(writer.getId(), userId)) {
                this.canDelete = true;
            } else {
                this.canDelete = false;
            }

            if (comment.getAnonymous()) {
                if (comment.getAnonymousOrder().equals(-1)) {
                    this.nickname = "익명(작성자)";
                } else {
                    this.nickname = "익명" + comment.getAnonymousOrder();
                }
            } else {
                this.profileImage = writer.getProfileImagePath();
                this.writer_id = writer.getId();
                this.nickname = writer.getNickName();
            }
        }
        this.heartCnt = comment.getHeartCnt();
        this.anonymous = comment.getAnonymous();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.time = comment.getTime();
        this.isBlocked = isBlocked;
        this.answers = subComments;
    }

    public CommentResponse(Comment comment, Long userId, Boolean isBlocked) {
        this.id = comment.getId();
        User writer = comment.getUser();
        if (writer != null) {
            if (Objects.equals(writer.getId(), userId)) {
                this.canDelete = true;
            } else {
                this.canDelete = false;
            }

            if (comment.getAnonymous()) {
                if (comment.getAnonymousOrder().equals(-1)) {
                    this.nickname = "익명(작성자)";
                } else {
                    this.nickname = "익명" + comment.getAnonymousOrder().toString();
                }
            } else {
                this.profileImage = writer.getProfileImagePath();
                this.writer_id = writer.getId();
                this.nickname = writer.getNickName();
            }
        }
        this.heartCnt = comment.getHeartCnt();
        this.anonymous = comment.getAnonymous();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.time = comment.getTime();
        this.isBlocked = isBlocked;
        this.answers = new ArrayList<>();
    }
}

