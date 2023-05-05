package FIS.iLUVit.config;

import FIS.iLUVit.config.argumentResolver.ForDB;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@ForDB
@Configuration
@EnableJpaRepositories(
        basePackages = "FIS.iLUVit.repository.iluvit",
        entityManagerFactoryRef = "iluvitEntityManager",
        transactionManagerRef = "iluvitTransactionManager"
)
public class IluvitJpaConfig {

    @Bean
    @ConfigurationProperties("spring.datasource-iluvit")
    public DataSourceProperties iluvitDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-iluvit.hikari")
    public HikariDataSource iluvitDataSource(DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean iluvitEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(iluvitDataSource(iluvitDatasourceProperties()));
        em.setPackagesToScan("FIS.iLUVit.domain.iluvit");
        em.setPersistenceUnitName("iluvitEntityManager");


        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public PlatformTransactionManager iluvitTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(iluvitEntityManager().getObject());
        return transactionManager;
    }

}
