package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	private final JobBuilderFactory jobs;

	private final StepBuilderFactory steps;

	@Bean
	public Step sampleStep() {
		return steps.get("sampleStep")
			.tasklet((con, chunkCtx) -> {
				System.out.println(">> sample step");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Job testJob() {
		return jobs.get("testJob")
			.start(sampleStep())
			.build();
	}
}
