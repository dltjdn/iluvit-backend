package FIS.iLUVit.domain.alarms;

import FIS.iLUVit.domain.BaseEntity;
import FIS.iLUVit.domain.User;
import org.springframework.context.MessageSource;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("null")
@DiscriminatorColumn(name = "dtype")
public abstract class Alarm extends BaseEntity {
    @Id @GeneratedValue
    protected Long id;
    protected String message;

    protected String mode;

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;

    public abstract Alarm createMessage(MessageSource messageSource);
}
