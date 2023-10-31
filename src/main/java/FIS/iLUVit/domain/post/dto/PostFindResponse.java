package FIS.iLUVit.domain.post.dto;

import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class PostFindResponse {
    @JsonProperty("post_id")
    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname; // 작성자 닉네임

    @JsonProperty("user_id")
    private Long userId; // 작성자 id
    private String profileImage; // 작성자 프로필 이미지
    private Integer commentCnt;
    private Integer heartCnt;
    private Integer imgCnt;
    private String previewImage;
    @JsonProperty("board_name")
    private String boardName;
    @JsonProperty("board_id")
    private Long boardId;
    private LocalDate date;
    private LocalTime time;
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;

    public static PostFindResponse of(Post post, String previewImage){
        PostFindResponseBuilder builder = PostFindResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .anonymous(post.getAnonymous())
                .nickname(post.getUser().getNickName())
                .userId(post.getUser().getId())
                .profileImage(post.getUser().getProfileImagePath())
                .commentCnt(post.getCommentCnt())
                .heartCnt(post.getHeartCnt())
                .imgCnt(post.getImgCnt())
                .boardName(post.getBoard().getName())
                .boardId(post.getBoard().getId())
                .date(post.getDate())
                .time(post.getTime())
                .previewImage(previewImage);

        if (post.getBoard().getCenter() != null) {
            builder.centerId(post.getBoard().getCenter().getId())
                    .centerName(post.getBoard().getCenter().getName());
        } else {
            builder.centerName("모두의 이야기");
        }
        return builder.build();
    }

}
