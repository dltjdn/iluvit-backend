package FIS.iLUVit.dto.post;

import FIS.iLUVit.domain.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private String nickname; // 작성자 닉네임
    private Long userId; // 작성자 id
    private String profileImage; // 작성자 프로필 이미지
    private int commentCnt;
    private int heartCnt;
    private int imgCnt;
    private String previewImage;
    private String board_name;
    private Long boardId;
    private LocalDate date;
    private LocalTime time;
    private Long centerId;
    private String centerName;

    @QueryProjection
    public PostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
        this.nickname = post.getUser().getNickName();
        this.userId = post.getUser().getId();
        this.profileImage = post.getUser().getProfileImagePath();
        this.commentCnt = post.getCommentCnt();
        this.heartCnt = post.getHeartCnt();
        this.imgCnt = post.getImgCnt();
        this.board_name = post.getBoard().getName();
        this.boardId = post.getBoard().getId();
        this.date = post.getDate();
        this.time = post.getTime();
        this.previewImage = post.getInfoImagePath();

        if (post.getBoard().getCenter() != null) {
            this.centerId = post.getBoard().getCenter().getId();
            this.centerName = post.getBoard().getCenter().getName();
        } else {
            this.centerName = "모두의 이야기";
        }
    }

    public void updatePreviewImage(List<String> encodedInfoImage) {
        this.previewImage = encodedInfoImage.isEmpty() ? null : encodedInfoImage.get(0);
    }
}
