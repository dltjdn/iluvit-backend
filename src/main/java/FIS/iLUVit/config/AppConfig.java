package FIS.iLUVit.config;

import FIS.iLUVit.config.argumentResolver.ForDB;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.TimeZone;

@Configuration
@ForDB
public class AppConfig {

    @PostConstruct
    public void start() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager){
        return new JPAQueryFactory(entityManager);
    }

    // 비밀번호 해싱 encoder 빈등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public DefaultMessageService defaultMessageService(@Value("${coolsms.api_key}") String api_key,
                                                       @Value("${coolsms.api_secret}") String api_secret,
                                                       @Value("${coolsms.domain}") String domain) {
        return NurigoApp.INSTANCE.initialize(api_key, api_secret, domain);
    }

}
