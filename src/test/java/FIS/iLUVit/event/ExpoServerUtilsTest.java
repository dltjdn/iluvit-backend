package FIS.iLUVit.event;

import FIS.iLUVit.domain.iluvit.ExpoToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class ExpoServerUtilsTest {

    @Test
    public void test() throws Exception {
        String recipient1 = "ExponentPushToken[FeQrt0GvJiT-1i1ClIgINc]";
//        String recipient2 = "ExponentPushToken[EhVXatNE-M34xo7aUDoRuP]";
        ExpoToken expoToken = ExpoToken.builder()
                .token(recipient1)
                .build();
        List<ExpoToken> recipients = new ArrayList<>(List.of(expoToken));
    }

    @Test
    public void androidTest() throws Exception {
        String token = "ExponentPushToken[fDu27bDrnVP47UpdPVxENF]";
        ExpoToken expoToken = ExpoToken.builder()
                .token(token)
                .accept(true)
                .build();
        List<ExpoToken> recipients = new ArrayList<>(List.of(expoToken));
        ExpoServerUtils.sendToExpoServer(recipients, "테스트용 메시지입니당");
    }

}