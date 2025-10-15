package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.partitioner.RebalancePartitioner;
import woojooin.planitbatch.batch.reader.RebalanceReader;
import woojooin.planitbatch.batch.writer.RebalanceWriter;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;
import woojooin.planitbatch.domain.rebalance.vo.Balance;
import woojooin.planitbatch.domain.rebalance.vo.Rebalance;
import woojooin.planitbatch.global.component.PartitionStepTimeLogger;

@Configuration
@RequiredArgsConstructor
public class RebalanceJob {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	private final JobExecutionTimeListener jobExecutionTimeListener;

	private final RebalanceReader rebalanceReader;
	private final ItemProcessor<Balance, Rebalance> rebalanceProcessor;
	private final RebalanceWriter rebalanceWriter;
	private final PartitionStepTimeLogger partitionStepTimeLogger;

	@Bean
	public RebalancePartitioner rebalancePartitioner(BalanceRepository balanceRepository) {
		return new RebalancePartitioner(balanceRepository);
	}

	@Bean
	public PartitionHandler partitionHandler(ThreadPoolTaskExecutor batchTaskExecutor) {
		TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
		handler.setTaskExecutor(batchTaskExecutor);
		handler.setStep(rebalanceSlaveStep());
		handler.setGridSize(4);
		return handler;
	}

	@Bean("rebalancingJob")
	public Job rebalancingJob(Step rebalanceMasterStep) {
		return jobBuilderFactory.get("rebalancingJob")
			.listener(jobExecutionTimeListener)
			.start(rebalanceMasterStep)
			.build();
	}

	@Bean
	public Step rebalanceSlaveStep() {
		return stepBuilderFactory.get("rebalanceSlaveStep")
			.<Balance, Rebalance>chunk(RebalanceReader.CHUNK_SIZE)
			.reader(rebalanceReader)
			.processor(rebalanceProcessor)
			.writer(rebalanceWriter)
			.listener(partitionStepTimeLogger)
			.build();
	}

	@Bean
	public Step rebalanceMasterStep(RebalancePartitioner partitioner,
		PartitionHandler partitionHandler) {
		return stepBuilderFactory.get("rebalanceMasterStep")
			.partitioner("rebalanceSlaveStep", partitioner)
			.partitionHandler(partitionHandler)
			.build();
	}

}
