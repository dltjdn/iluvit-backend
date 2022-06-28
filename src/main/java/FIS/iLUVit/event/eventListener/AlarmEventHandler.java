package FIS.iLUVit.event.eventListener;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmEventHandler {

    private final CenterRepository centerRepository;
    private final AlarmRepository alarmRepository;

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
        alarmRepository.save(alarm);
        log.info("알람생성 종료");
    }
}
