package FIS.iLUVit.config;

import FIS.iLUVit.aspect.trace.querycounter.HibernateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class HibernateCustomConfig implements HibernatePropertiesCustomizer {

    private final HibernateInterceptor hibernateInterceptor;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.ejb.interceptor", hibernateInterceptor);
    }
}
