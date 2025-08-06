package woojooin.planitbatch.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job isaTaxSavingJob;

	@Scheduled(cron = "0 * * * * ?")  // 매 분 실행 (테스트용)
	public void runIsaTaxSavingJob() throws Exception {
		log.info("🔄 배치 job 실행 시도");

		String jobName = isaTaxSavingJob.getName();

		// 1. 최근 실행된 JobInstance 가져오기
		for (JobInstance instance : jobExplorer.getJobInstances(jobName, 0, 10)) {
			for (JobExecution execution : jobExplorer.getJobExecutions(instance)) {
				if (execution.isRunning()) {
					log.warn("⚠️ Job '{}' 이(가) 아직 실행 중이므로, 새 실행을 건너뜁니다.", jobName);
					return;
				}
			}
		}

		// 2. 실행
		jobLauncher.run(isaTaxSavingJob, new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis()) // 항상 다른 파라미터로 실행
			.toJobParameters());
	}
}
