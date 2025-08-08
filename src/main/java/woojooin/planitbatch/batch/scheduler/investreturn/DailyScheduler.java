package woojooin.planitbatch.batch.scheduler.investreturn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static woojooin.planitbatch.batch.scheduler.investreturn.PastScheduler.FORMATTER;

@Component
@RequiredArgsConstructor
public class DailyScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier("dailybatchJob")
    private final Job dailybatchJob;

    @Scheduled(cron = "0 20 00 * * ?")
    public void runDailyReturnJob() throws Exception {
        LocalDate today = LocalDate.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", today.toString())         // 예: "2025-08-06"
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(dailybatchJob, jobParameters);

//       과거 etf 그래프에서 만든 자료

//        LocalDate startDate = LocalDate.of(2024, 8, 5);
//        LocalDate endDate = LocalDate.of(2025, 8, 5);
//
//        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
//            String dateStr = date.format(FORMATTER);
//            System.out.println("▶ " + dateStr + " 배치 시작");
//
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("today", dateStr)
//                    .addLong("time", System.currentTimeMillis())  // JobInstance 중복 방지
//                    .toJobParameters();
//
//            jobLauncher.run(dailybatchJob, jobParameters);
//        }
    }
}








