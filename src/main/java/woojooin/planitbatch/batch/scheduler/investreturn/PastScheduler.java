package woojooin.planitbatch.batch.scheduler.investreturn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PastScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier("etfDailyReturnJob")
    private final Job etfDailyReturnJob;


    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//    @Scheduled(cron = "0 20 19 * * ?")  //
    public void runDailyReturnJob() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 8, 5);
        LocalDate endDate = LocalDate.of(2025, 8, 5);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(FORMATTER);
            System.out.println("▶  " + dateStr + " 배치 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startDate", dateStr)
                    .addString("endDate", dateStr)  // 하루 단위
                    .addLong("time", System.currentTimeMillis())  // JobInstance 중복 방지
                    .toJobParameters();

            jobLauncher.run(etfDailyReturnJob, jobParameters);
        }
    }
}
