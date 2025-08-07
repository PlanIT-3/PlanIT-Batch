package woojooin.planitbatch.global.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:application.properties")
@MapperScan("woojooin.planitbatch.domain.mapper")
public class DatabaseConfig implements BatchConfigurer {

    @Value("${jdbc.driver}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${batch.jdbc.driver}")
    private String batchDriverClassName;

    @Value("${batch.jdbc.url}")
    private String batchUrl;

    @Value("${batch.jdbc.username}")
    private String batchUsername;

    @Value("${batch.jdbc.password}")
    private String batchPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        
        // 기존 애플리케이션 DB 풀 설정 (성능 최적화)
        config.setMaximumPoolSize(8);        // 최대 8개 연결로 증가
        config.setMinimumIdle(2);            // 최소 2개 연결 유지
        config.setConnectionTimeout(10000);  // 연결 대기 시간 10초
        config.setIdleTimeout(600000);       // 10분 후 유휴 연결 해제
        config.setMaxLifetime(1800000);      // 30분 후 연결 갱신
        config.setLeakDetectionThreshold(60000); // 1분 후 연결 누수 탐지
        
        // 연결 검증 설정
        config.setValidationTimeout(5000);
        config.setConnectionTestQuery("SELECT 1");
        
        // 풀 이름 설정
        config.setPoolName("PlanIT-Main-CP");
        
        // HikariCP 모니터링 및 로깅 강화
        config.setRegisterMbeans(true);           // JMX 모니터링 활성화
        
        // 추가 안정성 설정
        config.setInitializationFailTimeout(30000);  // 초기화 실패 타임아웃 30초
        config.setConnectionInitSql("SELECT 1");     // 커넥션 초기화 SQL

        return new HikariDataSource(config);
    }


    @Bean("batchDataSource")
    public DataSource batchDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(batchDriverClassName);
        config.setJdbcUrl(batchUrl);
        config.setUsername(batchUsername);
        config.setPassword(batchPassword);
        
        // 배치 메타데이터 DB 풀 설정 (최적화된 작은 풀 크기)
        config.setMaximumPoolSize(3);        // 최대 3개 연결로 제한
        config.setMinimumIdle(1);            // 최소 1개 연결 유지
        config.setConnectionTimeout(10000);  // 연결 대기 시간 10초
        config.setIdleTimeout(600000);       // 10분 후 유휴 연결 해제
        config.setMaxLifetime(1800000);      // 30분 후 연결 갱신
        config.setLeakDetectionThreshold(60000); // 1분 후 연결 누수 탐지
        
        // 연결 검증 설정
        config.setValidationTimeout(5000);
        config.setConnectionTestQuery("SELECT 1");
        
        // 풀 이름 설정
        config.setPoolName("PlanIT-Batch-CP");

        return new HikariDataSource(config);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        sessionFactory.setDataSource(dataSource());

        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml")
        );

        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    @Bean("batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(batchDataSource());
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource());
        factory.setTransactionManager(batchTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return batchTransactionManager();
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(batchDataSource());
        jobExplorerFactoryBean.afterPropertiesSet();
        return jobExplorerFactoryBean.getObject();
    }

}