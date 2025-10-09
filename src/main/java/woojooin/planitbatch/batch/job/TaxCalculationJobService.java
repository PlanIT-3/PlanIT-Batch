package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaxCalculationJobService {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job taxCalculationJob; // BatchConfig에서 정의한 chunk 방식 Job

	/**
	 * 세금 계산 Job을 chunk 1000개 방식으로 실행합니다.
	 */
	public void executeTaxCalculationJob() {

		log.info("=== 세금 계산 Job 시작 (Chunk 1000개 방식) ===");

		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.addString("jobName", "taxCalculationJob")
				.toJobParameters();

			JobExecution jobExecution = jobLauncher.run(taxCalculationJob, jobParameters);

			// 배치 실행 결과 상세 로깅
			BatchStatus status = jobExecution.getStatus();
			log.info("세금 계산 배치 실행 결과: {}", status);
			log.info("세금 계산 배치 시작 시간: {}", jobExecution.getStartTime());
			log.info("세금 계산 배치 종료 시간: {}", jobExecution.getEndTime());

			if (status == BatchStatus.FAILED) {
				log.error("세금 계산 배치 실행 실패. 실패 예외: {}", jobExecution.getAllFailureExceptions());
				throw new RuntimeException("세금 계산 배치 실행 실패");
			}

			log.info("=== 세금 계산 Job 완료 (Chunk 1000개 방식) ===");

		} catch (Exception e) {
			log.error("세금 계산 Job 실행 중 오류 발생", e);

			throw new RuntimeException("세금 계산 Job 실행 실패", e);
		}
	}
} 