package FIS.iLUVit.service;



import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class AuthNumberTest {

    @Test
    void test() throws InterruptedException {
        LocalDateTime a = LocalDateTime.now();
        Thread.sleep(1000);
        LocalDateTime b = LocalDateTime.now();
        Duration duration = Duration.between(a, b);
        System.out.println(duration.getSeconds());
    }
}
