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
public class ProductSchedular {

	private final JobLauncher jobLauncher;

	@Qualifier("productExpectedReturnRateJob")
	private final Job productExpectedReturnRateJob;

	/**
	 * product 예상 수익 계산 작업 배치
	 */
	@Scheduled(cron = "0 27 1 * * *", zone = "Asia/Seoul")
	public void runExpectedReturnResultBatch() {
		try {
			log.info("[Product Batch] - runExpectedReturnResultBatch() start ===>>>>>");
			JobParameters params = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(productExpectedReturnRateJob, params);
			log.info("[Product Batch] - runExpectedReturnResultBatch() finish ===>>>>>");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
