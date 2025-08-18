package woojooin.planitbatch.batch.scheduler.investreturn;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DailyReturnScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailyReturnJob;

    @Scheduled(cron = "0 10 00 * * ?")
    public void runDailyReturnJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", LocalDate.now().toString())
//                .addString("date", "2025-08-05") // 목데이터 날짜
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(dailyReturnJob, jobParameters);
    }
}

