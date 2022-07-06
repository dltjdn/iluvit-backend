package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ChatAlarm extends Alarm{

    @JoinColumn(name = "senderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    public ChatAlarm(User user, User sender) {
        super(user);
        this.mode = AlarmUtils.CHAT_RECEIVED;
        this.sender = sender;
        String[] args = {sender.getNickName()};
        this.message = AlarmUtils.getMessage(mode, args);
    }

    @Override
    public AlarmDto exportAlarm() {
        return new ChatAlarmDto(id, createdDate, message, dtype, sender.getId());
    }

    @Getter
    public static class ChatAlarmDto extends AlarmDto{

        protected Long senderId;
        public ChatAlarmDto(Long id, LocalDateTime createdDate, String message, String type, Long senderId) {
            super(id, createdDate, message, type);
            this.senderId = senderId;
        }
    }
}
