package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.batch.processor.IsaTaxSavingProcessor;
import woojooin.planitbatch.batch.reader.IsaTaxSavingReader;
import woojooin.planitbatch.batch.writer.IsaTaxSavingWriter;
import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

@Configuration
@RequiredArgsConstructor
public class IsaTaxSavingJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final IsaTaxSavingReader reader;
	private final IsaTaxSavingProcessor processor;
	private final IsaTaxSavingWriter writer;

	@Bean
	public Job isaTaxSavingJob(@Qualifier("isaTaxReader") ItemReader<UserProductQuarterData> reader) {
		Step isaTaxSavingStep = stepBuilderFactory.get("isaTaxSavingStep")
			.<UserProductQuarterData, IsaTaxSavingHistoryVo>chunk(100)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();

		return jobBuilderFactory.get("isaTaxSavingJob")
			.start(isaTaxSavingStep)
			.build();
	}



}