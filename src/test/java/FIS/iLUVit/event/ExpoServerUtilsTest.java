package FIS.iLUVit.event;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.event.eventListener.AlarmEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class ExpoServerUtilsTest {

    @Autowired
    AlarmEventHandler eventHandler;

    @Test
    public void test() throws Exception {
        String recipient1 = "ExponentPushToken[FeQrt0GvJiT-1i1ClIgINc]";
//        String recipient2 = "ExponentPushToken[EhVXatNE-M34xo7aUDoRuP]";
        ExpoToken expoToken = ExpoToken.builder()
                .token(recipient1)
                .build();
        List<ExpoToken> recipients = new ArrayList<>(List.of(expoToken));
    }

}