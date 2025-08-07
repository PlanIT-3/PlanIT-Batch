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

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	@Autowired
	private JobBuilderFactory jobs;
	@Autowired
	private StepBuilderFactory steps;
//
//	@Bean
//	public Step sampleStep() {
//		return steps.get("sampleStep")
//			.tasklet((con, chunkCtx) -> {
//				System.out.println(">> sample step");
//				return RepeatStatus.FINISHED;
//			})
//			.build();
//	}
//
//	@Bean
//	public Job testJob() {
//		return jobs.get("testJob")
//			.start(sampleStep())
//			.build();
//	}
}
