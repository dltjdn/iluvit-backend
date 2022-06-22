package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

import javax.persistence.*;

import static FIS.iLUVit.service.AlarmUtils.*;

@Entity
@NoArgsConstructor
@Getter
public class PostAlarm extends Alarm {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;


    @Override
    public Alarm createMessage(MessageSource messageSource, Mode mode) {
        return null;
    }
}

