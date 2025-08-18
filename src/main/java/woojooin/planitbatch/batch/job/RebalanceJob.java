package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.reader.RebalanceReader;
import woojooin.planitbatch.batch.writer.RebalanceWriter;
import woojooin.planitbatch.domain.rebalance.vo.Balance;
import woojooin.planitbatch.domain.rebalance.vo.Rebalance;

@Configuration
@RequiredArgsConstructor
public class RebalanceJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	private final JobExecutionTimeListener jobExecutionTimeListener;

	private final RebalanceReader rebalanceReader;
	private final ItemProcessor<Balance, Rebalance> rebalanceProcessor;
	private final RebalanceWriter rebalanceWriter;

	@Bean("rebalancingJob")
	public Job rebalancingJob() {
		return jobBuilderFactory.get("rebalancingJob")
			.start(rebalanceStep())
			.build();
	}

	@Bean("rebalanceStep")
	public Step rebalanceStep() {
		return stepBuilderFactory.get("rebalanceStep")
			.<Balance, Rebalance>chunk(rebalanceReader.CHUNK_SIZE)
			.reader(rebalanceReader)
			.processor(rebalanceProcessor)
			.writer(rebalanceWriter)
			.build();
	}
}
