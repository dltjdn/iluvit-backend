package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDetailDto;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.BoardErrorResult;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PostAlarm extends Alarm {

    @Column(name = "postId")
    private Long postId;

    @Column(name = "boardId")
    private Long boardId;

    @Column(name = "centerId")
    private Long centerId;
    private String boardName;
    private String centerName;
    private Boolean anonymous;
    private String commentUserProfileImage;
    private String commentUserNickname;

    public PostAlarm(User postWriter, Post post, Comment comment) {
        super(postWriter);
        this.mode = AlarmUtils.POST_COMMENT;
        this.postId = post.getId();
        this.boardId = post.getBoard().getId();
        if (post.getBoard() == null) {
            throw new BoardException(BoardErrorResult.BOARD_NOT_EXIST);
        }
        if(post.getBoard().getCenter() != null) {
            this.centerName = post.getBoard().getCenter().getName();
            this.centerId = post.getBoard().getCenter().getId();
        }
        this.boardName = post.getBoard().getName();
        this.anonymous = comment.getAnonymous();
        if(!this.anonymous){
            commentUserProfileImage = comment.getUser().getProfileImagePath();
            commentUserNickname = comment.getUser().getNickName();
        }
        String[] args = anonymous ? new String[]{post.getTitle(), "익명", comment.getContent()} :
                new String[]{post.getTitle(), comment.getUser().getNickName(), comment.getContent()};
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDetailDto exportAlarm() {
        return centerId == null ?
                new PostAlarmDto(id, boardName, createdDate, message, dtype, postId, anonymous, commentUserProfileImage, commentUserNickname, centerName, boardId, null) :
                new PostAlarmDto(id, boardName, createdDate, message, dtype, postId, anonymous, commentUserProfileImage, commentUserNickname, centerName, boardId, centerId);
    }

    @Getter
    public static class PostAlarmDto extends AlarmDetailDto {
        protected Long postId;
        private Long centerId;
        private Long boardId;
        private String boardName;
        private String centerName;
        private Boolean anonymous;
        private String commentUserProfileImage;
        private String commentUserNickname;

        public PostAlarmDto(Long id, String boardName, LocalDateTime createdDate, String message, String type, Long postId, Boolean anonymous, String commentUserProfileImage, String commentUserNickname, String centerName, Long boardId, Long centerId) {
            super(id, createdDate, message, type);
            this.postId = postId;
            this.boardName = boardName;
            this.anonymous = anonymous;
            this.commentUserNickname = commentUserNickname;
            this.commentUserProfileImage = commentUserProfileImage;
            this.centerName = centerName;
            this.centerId = centerId;
            this.boardId = boardId;
        }
    }
}

