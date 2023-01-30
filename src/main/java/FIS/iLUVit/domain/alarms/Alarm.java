package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.dto.alarm.AlarmDetailDto;
import FIS.iLUVit.domain.BaseEntity;
import FIS.iLUVit.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("null")
@DiscriminatorColumn(name = "dtype")
@Getter
@NoArgsConstructor
public abstract class Alarm extends BaseEntity {

    @Id @GeneratedValue
    protected Long id;
    protected String message;

    @Transient
    protected String mode;

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;

    public Alarm(User user) {
        this.user = user.updateReadAlarm(Boolean.FALSE);
    }

    public abstract AlarmDetailDto exportAlarm();

}
