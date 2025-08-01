package woojooin.planitbatch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@ComponentScan(basePackages = "woojooin.planitbatch")
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class, TestBatchConfig.class})
public class AppConfig {
}