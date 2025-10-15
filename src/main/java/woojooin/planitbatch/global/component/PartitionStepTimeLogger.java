package woojooin.planitbatch.global.component;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartitionStepTimeLogger extends StepExecutionListenerSupport {

	private long startTime;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		startTime = System.currentTimeMillis();
		log.info("[{}] Step started on thread: {}",
			stepExecution.getStepName(), Thread.currentThread().getName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		long endTime = System.currentTimeMillis();
		long elapsed = endTime - startTime;

		log.info("[{}] Step finished on thread: {} | elapsed={} ms ({} sec)",
			stepExecution.getStepName(),
			Thread.currentThread().getName(),
			elapsed,
			elapsed / 1000.0);
		return stepExecution.getExitStatus();
	}
}
