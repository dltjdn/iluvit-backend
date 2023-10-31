package FIS.iLUVit.domain.post.dto;

import FIS.iLUVit.domain.comment.dto.CommentInPostResponse;
import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Lob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostFindDetailResponse {

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

    public static PostFindDetailResponse of(Post post, List<String> infoImages, String profileImage, Long userId, List<CommentInPostResponse> commentInPostResponses){
        PostFindDetailResponseBuilder builder = PostFindDetailResponse.builder()
                .id(post.getId())
                .anonymous(post.getAnonymous())
                .date(post.getDate())
                .time(post.getTime())
                .title(post.getTitle())
                .content(post.getContent())
                .profileImage(profileImage)
                .images(infoImages)
                .imgCnt(post.getImgCnt())
                .heartCnt(post.getHeartCnt())
                .comments(commentInPostResponses)
                .commentCnt(post.getCommentCnt());

        if (post.getUser() != null) {
            builder.canDelete(Objects.equals(post.getUser().getId(), userId));

            if (post.getAnonymous()) {
                builder.nickname ("익명");
            } else {
                builder.writerId(post.getUser().getId());
                builder.nickname(post.getUser().getNickName());
            }
        }

        if (post.getBoard() != null) {
            builder.boardId(post.getBoard().getId());
            builder.boardName (post.getBoard().getName());
            if (post.getBoard().getCenter() != null) {
                builder.centerId(post.getBoard().getCenter().getId());
                builder.centerName(post.getBoard().getCenter().getName());
            }
        }
        return builder.build();

    }

}