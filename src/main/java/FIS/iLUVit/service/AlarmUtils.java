package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.event.AlarmEvent;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AlarmUtils {

    private static MessageSource messageSource;
    private static ApplicationEventPublisher eventPublisher;
    // 작성한 글에 댓글 알림
    public static final String POST_COMMENT = "alarm.post.comment";
    // 좋아요한 센터에서 설명회 생성됨
    public static final String PRESENTATION_CREATED_LIKED_CENTER = "alarm.presentation.createdLikedCenter";
    // 대기신청했던 거에서 참여로 바뀜
    public static final String PRESENTATION_WAITING_TO_PARTICIPATE = "alarm.presentation.waitingToParticipate";
    // 설명회 신청인원 가득 참
    public static final String PRESENTATION_APPLICANTS_FULL = "alarm.presentation.applicantsFull";
    // 설명회 신청기간 종료
    public static final String PRESENTATION_CLOSED = "alarm.presentation.closed";
    // 채팅 수신됨
    public static final String CHAT_RECEIVED = "alarm.chat.received";
    //
    public static final String CENTER_APPROVAL_RECEIVED = "alarm.center.approvalReceived";
    //
    public static final String CENTER_APPROVAL_ACCEPTED = "alarm.center.approvalAccepted";

    @Autowired
    public AlarmUtils(MessageSource messageSource, ApplicationEventPublisher eventPublisher){
        this.messageSource = messageSource;
        this.eventPublisher = eventPublisher;
    }

    public static String getMessage(String code, Object[] args){
        System.out.println(CHAT_RECEIVED);
        return messageSource.getMessage(code, args, null);
    }

    public static AlarmEvent publishAlarmEvent(Alarm alarm){
        AlarmEvent alarmEvent = new AlarmEvent(alarm);
        eventPublisher.publishEvent(alarmEvent);
        return alarmEvent;
    }

}
