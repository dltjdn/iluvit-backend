package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.event.dto.ExpoServerResponse;
import FIS.iLUVit.event.ExpoServerUtils;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ExpoTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AlarmEventHandler {

    private final CenterRepository centerRepository;
    private final AlarmRepository alarmRepository;
    private final ExpoTokenRepository expoTokenRepository;

//    @Async
//    @EventListener
//    public void test123(AlarmEvent alarmEvent) throws InterruptedException {
//        log.info("do something");
//        // 쿼리 나간다, transaction 은 thread 간 공유 없다.
//        centerRepository.findById(1L);
//        // 여기서 오류 터져도 상관없음
//        Thread.sleep(2000);
//        log.info("stop something");
//    }

    @Async
    @EventListener(AlarmEvent.class)
    public void saveAlarm(AlarmEvent alarmEvent){
        log.info("알람 생성 중");
        Alarm alarm = alarmEvent.getAlarm();
        User user = alarm.getUser();
        log.info(alarm.getMessage());
        alarmRepository.save(alarm);

        List<ExpoToken> expoTokens = expoTokenRepository.findByUser(user);
        ExpoServerResponse response = ExpoServerUtils.sendToExpoServer(expoTokens, alarm.getMessage());
        handleTokenSendingError(response);

        log.info("알람생성 종료");
    }

    /**
     * handleTokenSendingError + removeInvalidToken
     * 작성자: 이창윤
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
