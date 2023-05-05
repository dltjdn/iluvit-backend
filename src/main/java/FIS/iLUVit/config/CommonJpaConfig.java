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
        basePackages = "FIS.iLUVit.repository.common",
        entityManagerFactoryRef = "commonEntityManager",
        transactionManagerRef = "commonTransactionManager"
)
public class CommonJpaConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource-common")
    public DataSourceProperties commonDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource-common.hikari")
    public HikariDataSource commonDataSource(DataSourceProperties properties) {
            return properties
                    .initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean commonEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(commonDataSource(commonDatasourceProperties()));
        em.setPackagesToScan("FIS.iLUVit.domain.common");
        em.setPersistenceUnitName("commonEntityManager");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager commonTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(commonEntityManager().getObject());
        return transactionManager;
    }

}