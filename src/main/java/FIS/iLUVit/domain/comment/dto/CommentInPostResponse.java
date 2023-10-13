package FIS.iLUVit.domain.comment.dto;

import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.user.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CommentInPostResponse {
    private Long id;
    @JsonProperty("writer_id")
    private Long writerId;
    private String nickName;
    private String profileImage;
    private String content;
    private Integer heartCnt;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;
    private Boolean canDelete;
    private Boolean isBlocked;  // 댓글 차단 여부
    private List<CommentInPostResponse> answers;

    public static CommentInPostResponse commentOf(Comment comment, Long userId, List<CommentInPostResponse> subComments, Boolean isBlocked){
        CommentInPostResponseBuilder builder = CommentInPostResponse.builder()
                .id(comment.getId())
                .heartCnt(comment.getHeartCnt())
                .anonymous(comment.getAnonymous())
                .content(comment.getContent())
                .date(comment.getDate())
                .time(comment.getTime())
                .isBlocked(isBlocked)
                .answers(subComments);

        User user = comment.getUser();
        if (user != null) {
            builder.writerId(user.getId());
            builder.canDelete(Objects.equals(user.getId(), userId));

            if (comment.getAnonymous()) {
                if (comment.getAnonymousOrder().equals(-1)) {
                    builder.nickName("익명(작성자)");
                } else {
                    builder.nickName("익명" + comment.getAnonymousOrder());
                }
            } else {
                builder.profileImage(user.getProfileImagePath());
                builder.nickName(user.getNickName());
            }
        }
        return builder.build();
    }

    public static CommentInPostResponse subCommentOf(Comment comment, Long userId, Boolean isBlocked){
        CommentInPostResponseBuilder builder = CommentInPostResponse.builder()
                .id(comment.getId())
                .heartCnt(comment.getHeartCnt())
                .anonymous(comment.getAnonymous())
                .content(comment.getContent())
                .date(comment.getDate())
                .time(comment.getTime())
                .isBlocked(isBlocked);

        User user = comment.getUser();
        if (user != null) {
            builder.writerId(user.getId());
            builder.canDelete(Objects.equals(user.getId(), userId));

            if (comment.getAnonymous()) {
                if (comment.getAnonymousOrder().equals(-1)) {
                    builder.nickName("익명(작성자)");
                } else {
                    builder.nickName("익명" + comment.getAnonymousOrder());
                }
            } else {
                builder.profileImage(user.getProfileImagePath());
                builder.nickName(user.getNickName());
            }
        }
        return builder.build();
    }





}