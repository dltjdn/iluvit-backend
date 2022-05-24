package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPostResponse {

    private String nickname;
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

    private List<GetCommentResponse> comments;
    private Integer commentCnt;

    @QueryProjection
    public GetPostResponse(Post post, List<String> encodedImages, String encodedProfileImage) {
        this.nickname = post.getUser().getNickName();
        this.date = post.getDate();
        this.time = post.getTime();
        this.title = post.getTitle();
        this.content = post.getContent();

        this.profileImage = encodedProfileImage;
        this.images = encodedImages;

        this.imgCnt = encodedImages.size();
        this.heartCnt = post.getHeartCnt();

        if (post.getBoard() == null) {
            this.boardName = "모두의 게시판";
        } else {
            this.boardName = post.getBoard().getName();
            if (post.getBoard().getCenter() != null) {
                this.centerName = post.getBoard().getCenter().getName();
            }
        }

        this.comments = post.getComments().stream()
                .map(c -> new GetCommentResponse(c)).collect(Collectors.toList());
        this.commentCnt = post.getCommentCnt();

    }
}
