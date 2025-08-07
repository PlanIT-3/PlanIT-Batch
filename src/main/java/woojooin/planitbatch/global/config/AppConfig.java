package woojooin.planitbatch.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "woojooin.planitbatch")
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class})
public class AppConfig {
}