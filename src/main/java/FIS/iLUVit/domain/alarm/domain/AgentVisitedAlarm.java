package FIS.iLUVit.domain.alarm.domain;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.alarm.dto.AlarmResponse;
import FIS.iLUVit.domain.alarm.AlarmUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class AgentVisitedAlarm extends Alarm {
    @Enumerated(EnumType.STRING)
    private Auth auth;
    private Long centerId;
    private String centerName;

    public AgentVisitedAlarm(User teacher, Auth auth, Center center) {
        super(teacher);
        this.auth = auth;
        this.centerName = center.getName();
        this.centerId = center.getId();
        this.mode = AlarmUtils.AGENT_VISITED;
        message = AlarmUtils.getMessage(mode, null);
    }

    @Override
    public AlarmResponse exportAlarm() {
        return new AgentVisitedAlarmResponse(id, createdDate, message, dtype, auth, centerName, centerId);
    }

    @Getter
    public static class AgentVisitedAlarmResponse extends AlarmResponse {
        private Auth auth;
        private String centerName;
        private Long centerId;

        public AgentVisitedAlarmResponse(Long id, LocalDateTime createdTime, String message, String type, Auth auth, String centerName, Long centerId) {
            super(id, createdTime, message, type);
            this.auth = auth;
            this.centerName = centerName;
            this.centerId = centerId;
        }
    }
}
