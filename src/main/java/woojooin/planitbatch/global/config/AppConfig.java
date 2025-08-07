package woojooin.planitbatch.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class})
@ComponentScan(
	basePackages = "woojooin.planitbatch",
	// 컨트롤러 제외 스캔
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ANNOTATION,
		classes = {Controller.class, RestController.class}
	)
)
public class AppConfig {
}