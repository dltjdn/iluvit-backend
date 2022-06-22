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
    public static final Mode POST_COMMENT = Mode.POST_COMMENT;
    // 좋아요한 센터에서 설명회 생성됨
    public static final Mode PRESENTATION_CREATED_LIKED_CENTER = Mode.PRESENTATION_CREATED_LIKED_CENTER;
    // 대기신청했던 거에서 참여로 바뀜
    public static final Mode PRESENTATION_WAITING_TO_PARTICIPATE = Mode.PRESENTATION_WAITING_TO_PARTICIPATE;
    // 설명회 신청인원 가득 참
    public static final Mode PRESENTATION_APPLICANTS_FULL = Mode.PRESENTATION_APPLICANTS_FULL;
    // 설명회 신청기간 종료
    public static final Mode PRESENTATION_CLOSED = Mode.PRESENTATION_CLOSED;
    // 채팅 수신됨
    public static final Mode CHAT_RECEIVED = Mode.CHAT_RECEIVED;

    public enum Mode {
        POST_COMMENT("alarm.post.comment"),
        PRESENTATION_CREATED_LIKED_CENTER("alarm.presentation.createdLikedCenter"),
        PRESENTATION_WAITING_TO_PARTICIPATE("alarm.presentation.waitingToParticipate"),
        PRESENTATION_APPLICANTS_FULL("alarm.presentation.applicantsFull"),
        PRESENTATION_CLOSED("alarm.presentation.closed"),
        CHAT_RECEIVED("alarm.chat.received");

        private String path;

        Mode(String path) {
            this.path = path;
        }
    }

    @Autowired
    public AlarmUtils(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    public static Alarm createMessage(Alarm alarm, Mode mode){
        alarm.createMessage(messageSource, mode);
        return alarm;
    }



}
