package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.Chat;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.service.MessageUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@Getter
public class ChatAlarm extends Alarm{

    @JoinColumn(name = "senderId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    public ChatAlarm(User user, User sender) {
        super(user);
        this.mode = MessageUtils.CHAT_RECEIVED;
        this.sender = sender;
        String[] args = {sender.getNickName()};
        this.message = MessageUtils.getMessage(mode, args);
    }
}