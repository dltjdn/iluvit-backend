package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class PostAlarm extends Alarm {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @Override
    public Alarm createMessage(MessageSource messageSource) {
        return null;
    }
}

