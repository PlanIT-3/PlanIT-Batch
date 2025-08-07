package woojooin.planitbatch.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job isaTaxSavingJob;

	@Scheduled(cron = "0 31 10 * * *")  // 매일 2시 20분에 실행
	public void runIsaTaxSavingJob() {
		String jobName = isaTaxSavingJob.getName();

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("timestamp", System.currentTimeMillis()) // 매 실행마다 달라지는 값
			.toJobParameters();

		try {
			// 현재 실행 중인 동일 Job이 있는지 확인 (jobName 기준)
			if (!jobExplorer.findRunningJobExecutions(jobName).isEmpty()) {
				log.warn("[스케줄러] Job '{}'이(가) 이미 실행 중입니다. 실행을 건너뜁니다.", jobName);
				return;
			}

			log.info("[스케줄러] Job '{}' 실행을 시작합니다.", jobName);
			JobExecution jobExecution = jobLauncher.run(isaTaxSavingJob, jobParameters);
			log.info("[스케줄러] Job '{}' 실행 상태: {}", jobName, jobExecution.getStatus());

		} catch (Exception e) {
			log.error("[스케줄러] Job '{}' 실행 중 예외가 발생했습니다.", jobName, e);
		}
	}
}
