package FIS.iLUVit.config;

import FIS.iLUVit.config.argumentResolver.ForDB;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@ForDB
public class QueryDslConfig {

    @PersistenceContext(unitName = "commonEntityManager")
    private EntityManager commonEntityManager;

    @PersistenceContext(unitName = "iLuvitEntityManager")
    private EntityManager iluvitEntityManager;

    @Bean
    @Primary
    public JPAQueryFactory commonJpaQueryFactory() {
        return new JPAQueryFactory(commonEntityManager);
    }

    @Bean
    public JPAQueryFactory iluvitJpaQueryFactory() {
        return new JPAQueryFactory(iluvitEntityManager);
    }

}
