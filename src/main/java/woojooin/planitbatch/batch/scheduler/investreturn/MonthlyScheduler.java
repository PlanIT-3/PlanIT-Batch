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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class MonthlyScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier("monthlybatchJob")
    private final Job monthlybatchJob;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(cron = "0 30 0 L * ?")
    public void runMonthlyReturnJob() throws Exception {
        LocalDate today = LocalDate.now(); // 예: 2025-09-01

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", today.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(monthlybatchJob, jobParameters);
    }
}

//과거 etf 저료 넣은 것
//        LocalDate startDate = LocalDate.of(2024, 8, 5);
//        LocalDate endDate = LocalDate.of(2025, 8, 5);
//
//        LocalDate current = startDate.with(TemporalAdjusters.firstDayOfNextMonth());
//        if (startDate.getDayOfMonth() == 1) {
//            current = startDate;
//        }
//
//        for (; !current.isAfter(endDate); current = current.plusMonths(1)) {
//            // 그 달의 마지막 날로 재조정
//            LocalDate lastOfMonth = current.with(TemporalAdjusters.lastDayOfMonth());
//            String dateStr = lastOfMonth.format(FORMATTER);
//            System.out.println("▶ 월별 기준 날짜: " + dateStr + " 배치 시작");
//
//            var params = new JobParametersBuilder()
//                    .addString("today", dateStr)
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//            jobLauncher.run(monthlybatchJob, params);
//        }
//    }

//}
