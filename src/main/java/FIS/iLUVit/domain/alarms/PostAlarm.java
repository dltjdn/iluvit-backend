package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
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

    public PostAlarm(User postWriter, Post post, Comment comment) {
        super(postWriter);
        String[] args = {post.getTitle(), comment.getContent()};
        this.mode = AlarmUtils.POST_COMMENT;
        this.post = post;
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new PostAlarmDto(id, createdDate, message, dtype, post.getId());
    }

    @Getter
    public static class PostAlarmDto extends AlarmDto{
        protected Long postId;

        public PostAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long postId) {
            super(id, createdDate, message, type);
            this.postId = postId;
        }
    }
}

