package FIS.iLUVit;

import FIS.iLUVit.event.AlarmEvent;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


public class Eventdfd {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    public void dd() throws Exception {
        //given
        runner.withBean(SomeEventListener.class)
                .run(context -> {
                            context.publishEvent(new AlarmEvent());
                        });
        //when

        //then
    }


    @Component
    public static class SomeEventListener {

        @EventListener(AlarmEvent.class)
        public void listen(AlarmEvent alarmEvent) {
            System.out.println("dddd");
        }
    }
}
