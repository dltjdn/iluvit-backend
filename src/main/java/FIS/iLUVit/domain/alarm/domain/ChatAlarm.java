package FIS.iLUVit.domain.alarm.domain;

import FIS.iLUVit.domain.alarm.dto.AlarmResponse;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.alarm.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ChatAlarm extends Alarm {
    private Boolean anonymous;
    private Long senderId;
    private String senderName;
    private String profileImage;

    public ChatAlarm(User user, User sender, Boolean anonymous) {
        super(user);
        this.mode = AlarmUtils.CHAT_RECEIVED;
        String[] args = anonymous ? new String[]{"익명"} : new String[]{sender.getNickName()};
        this.message = AlarmUtils.getMessage(mode, args);
        this.anonymous = anonymous;
        this.senderId = sender.getId();
        this.senderName = sender.getNickName();
        this.profileImage = anonymous ? "basic" : sender.getProfileImagePath();
    }

    @Override
    public AlarmResponse exportAlarm() {
        return anonymous ? new ChatAlarmResponse(id, createdDate, message, dtype, true, null, null, profileImage)
                : new ChatAlarmResponse(id, createdDate, message, dtype, false, senderId, senderName, profileImage);
    }


    @Getter
    public static class ChatAlarmResponse extends AlarmResponse {
        protected Boolean anonymous;
        protected Long senderId;
        protected String senderName;

        protected String profileImage;

        public ChatAlarmResponse(Long id, LocalDateTime createdDate, String message, String type, Boolean anonymous, Long senderId, String senderName, String profileImage) {
            super(id, createdDate, message, type);
            this.anonymous = anonymous;
            this.senderId = senderId;
            this.senderName = senderName;
            this.profileImage = profileImage;
        }
    }
}