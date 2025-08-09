package woojooin.planitbatch.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RebalanceScheduler {

	private final JobLauncher jobLauncher;

	@Qualifier("rebalancingJob")
	private final Job rebalancingJob;

	/**
	 * 리밸런스 산출 배치
	 * 매일 01:35 KST 실행
	 */
	@Scheduled(cron = "0 41 12 * * *", zone = "Asia/Seoul")
	public void runRebalanceJob() {
		try {
			log.info("[Rebalance Batch] - runRebalanceJob() start ===>>>>>");
			JobParameters params = new JobParametersBuilder()
				.addLong("ts", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(rebalancingJob, params);
			log.info("[Rebalance Batch] - runRebalanceJob() finish ===>>>>>");
		} catch (Exception e) {
			log.error("[Rebalance Batch] - error", e);
		}
	}
}