package woojooin.planitbatch.global.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ExecutorMetricConfig {

	private final MeterRegistry registry;

	@Bean
	public ThreadPoolTaskExecutor batchExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(16);
		executor.setThreadNamePrefix("rebalance-");
		executor.initialize();

		new ExecutorServiceMetrics(
			executor.getThreadPoolExecutor(),
			"batch-executor",
			Collections.emptyList()
		).bindTo(registry);

		return executor;
	}
}
