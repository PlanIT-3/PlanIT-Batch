package woojooin.planitbatch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job testJob; // 실행 Job

    @Scheduled(cron = "0 46 13 * * ?") // 트리거 타임
    public void runTestJob() {
        try {
            // JobParameters jobParameters = new JobParametersBuilder()
            //     .addLong("timestamp", System.currentTimeMillis())
            //     .toJobParameters();
            //
            // jobLauncher.run(testJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}