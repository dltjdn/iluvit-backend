package FIS.iLUVit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ILuVitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ILuVitApplication.class, args);
	}

}
