package woojooin.planitbatch.global.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
@Configuration
@EnableBatchProcessing
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
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // 스케줄링 스레드 풀 최적화
        scheduler.setPoolSize(3);                           // 스레드 풀 크기: 10개 → 3개 (커넥션 절약)
        scheduler.setThreadNamePrefix("batch-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 종료시 작업 완료 대기
        scheduler.setAwaitTerminationSeconds(60);            // 최대 60초 대기
        scheduler.setRejectedExecutionHandler(
            new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy() // 거부시 호출자 스레드에서 실행
        );
        
        scheduler.initialize();
        return scheduler;
    }
}