package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

}

