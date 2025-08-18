package woojooin.planitbatch.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyBalanceScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("memberDailyBalanceJob")
    private Job dailyBalanceJob;

    @Scheduled(cron = "0 27 20 * * ?")
    public void runDailyBalanceJob() {
        try {
            log.info("Daily Balance Job 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

            jobLauncher.run(dailyBalanceJob, jobParameters);

            log.info("Daily Balance Job 완료");

        } catch (Exception e) {
            log.error("Daily Balance Job 실행 실패: {}", e.getMessage(), e);
        }
    }
}