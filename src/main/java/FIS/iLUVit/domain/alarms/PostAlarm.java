package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.BoardErrorResult;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PostAlarm extends Alarm {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;
    private String boardName;
    private Boolean anonymous;
    private String commentUserProfileImage;
    private String commentUserNickname;

    public PostAlarm(User postWriter, Post post, Comment comment) {
        super(postWriter);
        this.mode = AlarmUtils.POST_COMMENT;
        this.post = post;
        if (post.getBoard() == null) {
            throw new BoardException(BoardErrorResult.BOARD_NOT_EXIST);
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
    public AlarmDto exportAlarm() {
        return new PostAlarmDto(id, boardName, createdDate, message, dtype, post.getId(), anonymous, commentUserProfileImage, commentUserNickname);
    }

    @Getter
    public static class PostAlarmDto extends AlarmDto{
        protected Long postId;
        private String boardName;
        private Boolean anonymous;
        private String commentUserProfileImage;
        private String commentUserNickname;

        public PostAlarmDto(Long id, String boardName, LocalDateTime createdDate, String message, String type, Long postId, Boolean anonymous, String commentUserProfileImage, String commentUserNickname) {
            super(id, createdDate, message, type);
            this.postId = postId;
            this.boardName = boardName;
            this.anonymous = anonymous;
            this.commentUserNickname = commentUserNickname;
            this.commentUserProfileImage = commentUserProfileImage;
        }
    }
}

