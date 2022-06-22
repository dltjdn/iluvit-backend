package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class AlarmUtils {

    private static MessageSource messageSource;
    // 작성한 글에 댓글 알림
    public static final String POST_COMMENT = "";
    //
    public static final String PRESENTATION_ = "";
    public static final String POST_COMMENT = "";
    public static final String POST_COMMENT = "";
    public static final String POST_COMMENT = "";
    public static final String POST_COMMENT = "";

    @Autowired
    public AlarmUtils(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    public static Alarm createMessage(Alarm alarm){
        alarm.createMessage(messageSource);
        return alarm;
    }



}
