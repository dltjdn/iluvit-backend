package FIS.iLUVit.domain.scrap.dto;

import FIS.iLUVit.domain.scrap.domain.ScrapPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ScrapDirPostsResponse {
    @JsonProperty("post_id")
    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname;    // 작성자 닉네임
    @JsonProperty("user_id")
    private Long userId;        // 작성자 id
    private int commentCnt;
    private int heartCnt;
    private int imgCnt;

    private String previewImage;
    @JsonProperty("board_id")
    private Long boardId;
    @JsonProperty("board_name")
    private String boardName;

    private LocalDate date;
    private LocalTime time;
    @JsonProperty("center_id")
    private Long centerId;

    @JsonProperty("center_name")
    private String centerName;

    @JsonProperty("scrapPost_id")
    private Long scrapPostId;


    public static ScrapDirPostsResponse from(ScrapPost scrapPost){
        ScrapDirPostsResponseBuilder builder = ScrapDirPostsResponse.builder()
                .scrapPostId(scrapPost.getId())
                .postId(scrapPost.getPost().getId())
                .title(scrapPost.getPost().getTitle())
                .content(scrapPost.getPost().getContent())
                .anonymous(scrapPost.getPost().getAnonymous())
                .nickname(scrapPost.getPost().getUser().getNickName())
                .userId(scrapPost.getPost().getUser().getId())
                .commentCnt(scrapPost.getPost().getCommentCnt())
                .heartCnt(scrapPost.getPost().getHeartCnt())
                .imgCnt(scrapPost.getPost().getImgCnt())
                .boardId(scrapPost.getPost().getBoard().getId())
                .boardName(scrapPost.getPost().getBoard().getName())
                .date(scrapPost.getPost().getDate())
                .time(scrapPost.getPost().getTime());

        if(scrapPost.getPost().getBoard().getCenter() != null){
            builder.centerId(scrapPost.getPost().getBoard().getCenter().getId())
                    .centerName(scrapPost.getPost().getBoard().getCenter().getName());
        }
        return builder.build();
    }
}