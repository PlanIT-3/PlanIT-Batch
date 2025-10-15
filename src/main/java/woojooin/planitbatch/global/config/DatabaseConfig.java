package woojooin.planitbatch.global.config;

import java.util.Collections;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
@MapperScan(basePackages = {
	"woojooin.planitbatch.domain.mapper",
	"woojooin.planitbatch.domain.product.mapper",
	"woojooin.planitbatch.domain.rebalance.mapper"
})
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

	private final MeterRegistry registry;

	public DatabaseConfig(MeterRegistry registry) {
		this.registry = registry;
	}

	@Bean
	@Primary
	public DataSource dataSource() {
		log.info("Main DB 연결 설정: {}", url);

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setPoolName("planitDataSource");
		config.setMaximumPoolSize(10);
		config.setConnectionTimeout(20000);
		config.setIdleTimeout(300000);
		config.setMaxLifetime(1200000);

		HikariDataSource ds = new HikariDataSource(config);
		registerHikariMetrics(ds, "planitDataSource");
		return ds;
	}

	@Bean("batchDataSource")
	public DataSource batchDataSource() {
		log.info("Batch 전용 DB 연결 설정: {}", batchUrl);

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(batchDriverClassName);
		config.setJdbcUrl(batchUrl);
		config.setUsername(batchUsername);
		config.setPassword(batchPassword);
		config.setPoolName("planitBatchDataSource");
		config.setMaximumPoolSize(5);
		config.setConnectionTimeout(20000);
		config.setIdleTimeout(300000);
		config.setMaxLifetime(1200000);

		HikariDataSource ds = new HikariDataSource(config);
		registerHikariMetrics(ds, "planitBatchDataSource");
		return ds;
	}

	/**
	 * ✅ HikariCP 메트릭을 수동 Gauge로 등록
	 */
	private void registerHikariMetrics(HikariDataSource ds, String poolName) {
		registry.gauge("hikaricp_connections_active",
			Collections.singletonList(Tag.of("pool", poolName)),
			ds, d -> d.getHikariPoolMXBean().getActiveConnections());

		registry.gauge("hikaricp_connections_idle",
			Collections.singletonList(Tag.of("pool", poolName)),
			ds, d -> d.getHikariPoolMXBean().getIdleConnections());

		registry.gauge("hikaricp_connections_pending",
			Collections.singletonList(Tag.of("pool", poolName)),
			ds, d -> d.getHikariPoolMXBean().getThreadsAwaitingConnection());

		registry.gauge("hikaricp_connections_max",
			Collections.singletonList(Tag.of("pool", poolName)),
			ds, d -> d.getHikariConfigMXBean().getMaximumPoolSize());

		registry.gauge("hikaricp_connections_min",
			Collections.singletonList(Tag.of("pool", poolName)),
			ds, d -> d.getHikariConfigMXBean().getMinimumIdle());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		sessionFactory.setMapperLocations(
			new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
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
		jobLauncher.setTaskExecutor(batchTaskExecutor(registry));
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

	@Bean
	public ThreadPoolTaskExecutor batchTaskExecutor(MeterRegistry registry) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(16);
		executor.setThreadNamePrefix("batch-executor-");
		executor.initialize();

		ExecutorServiceMetrics.monitor(
			registry,
			executor.getThreadPoolExecutor(),
			"batch-executor",
			Collections.emptyList()
		);

		return executor;
	}
}
