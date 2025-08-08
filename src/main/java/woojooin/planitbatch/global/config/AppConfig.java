package woojooin.planitbatch.global.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@ComponentScan(basePackages = "woojooin.planitbatch",
        // 컨트롤러 제외 스캔
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Controller.class, RestController.class}
        )
)
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class})
public class AppConfig {
}

