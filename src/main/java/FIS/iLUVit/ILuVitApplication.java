package FIS.iLUVit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.web.FilterChainProxy;

@SpringBootApplication
@EnableAsync
public class ILuVitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ILuVitApplication.class, args);
		FilterChainProxy bean = run.getBean(FilterChainProxy.class);
		System.out.println(bean);
	}

}
