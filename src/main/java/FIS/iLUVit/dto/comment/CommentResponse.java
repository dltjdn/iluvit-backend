package FIS.iLUVit.dto.comment;

import FIS.iLUVit.domain.iluvit.Comment;
import FIS.iLUVit.domain.iluvit.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
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

    private List<CommentResponse> answers;

    public CommentResponse(Comment comment, Long userId) {
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
        this.answers = comment.getSubComments().stream()
                .map(c -> new CommentResponse(c, userId))
                .collect(Collectors.toList());
    }
}

