package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.event.ExpoServerUtils;
import FIS.iLUVit.event.dto.ExpoServerResponse;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.ExpoTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class AlarmEventHandler {
    private final ExpoTokenRepository expoTokenRepository;

    @Async
    @EventListener(AlarmEvent.class)
    public void saveAlarm(AlarmEvent alarmEvent) {
        Alarm alarm = alarmEvent.getAlarm();
        String type = alarmEvent.getType();
        User user = alarm.getUser();

        List<ExpoToken> expoTokens = expoTokenRepository.findByUser(user);

        ExpoServerResponse response = ExpoServerUtils.sendToExpoServer(expoTokens, type, alarm.getMessage());

        handleTokenSendingError(response);
    }

    /**
     * handleTokenSendingError + removeInvalidToken
     * 내용: 엑스포 토큰 전송 결과에 에러 발생한 경우 해당 토큰을 삭제함
     */
    private void handleTokenSendingError(ExpoServerResponse response) {
        if (response.getExpoResponseList() != null) {
            removeInvalidToken(response);
        }
    }

    private void removeInvalidToken(ExpoServerResponse response) {
        List<String> invalidTokens = response.getExpoResponseList()
                .stream()
                .filter(i -> Objects.equals(i.getStatus(), "error"))
                .map(i -> i.getExpoDetailDto().getExpoPushToken())
                .collect(Collectors.toList());

        if (invalidTokens.isEmpty()) {
            return;
        }

        expoTokenRepository.deleteByTokenIn(invalidTokens);
    }

}
