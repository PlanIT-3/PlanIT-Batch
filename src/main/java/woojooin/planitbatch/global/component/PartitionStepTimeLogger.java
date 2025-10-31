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
			stepExecution.getStepName(),
			Thread.currentThread().getName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		long endTime = System.currentTimeMillis();
		long elapsed = endTime - startTime;

		// ✅ 기존 실행시간 로그 + 처리 카운트 로깅 추가
		log.info(
			"[{}] Step finished on thread: {} | elapsed={} ms ({} sec) | readCount={} | writeCount={} | commitCount={} | skipCount={}",
			stepExecution.getStepName(),
			Thread.currentThread().getName(),
			elapsed,
			elapsed / 1000.0,
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getCommitCount(),
			stepExecution.getSkipCount()
		);

		return stepExecution.getExitStatus();
	}
}
