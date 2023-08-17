package FIS.iLUVit.dto.comment;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentReplyResponse {
    private Long id;
    private Long writerId;
    private String nickName;
    private String profileImage;
    private String content;
    private Integer heartCnt;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;
    private Boolean canDelete;

    public CommentReplyResponse(Comment comment, Long userId) {
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
                    this.nickName = "익명(작성자)";
                } else {
                    this.nickName = "익명" + comment.getAnonymousOrder().toString();
                }
            } else {
                this.profileImage = writer.getProfileImagePath();
                this.writerId = writer.getId();
                this.nickName = writer.getNickName();
            }
        }
        this.heartCnt = comment.getHeartCnt();
        this.anonymous = comment.getAnonymous();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.time = comment.getTime();
    }
}
