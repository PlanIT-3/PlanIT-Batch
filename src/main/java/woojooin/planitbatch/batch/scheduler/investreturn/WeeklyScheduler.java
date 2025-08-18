package woojooin.planitbatch.batch.scheduler.investreturn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static woojooin.planitbatch.batch.scheduler.investreturn.PastScheduler.FORMATTER;

@Component
@RequiredArgsConstructor
public class WeeklyScheduler {
    private final JobLauncher jobLauncher;

    @Qualifier("WeeklyBatchJob")
    private final Job WeeklyBatchJob;


    @Scheduled(cron = "0 40 0 ? * MON")
    public void runWeeklyBatchJob() throws JobExecutionException {
        LocalDate today = LocalDate.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today",today.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(WeeklyBatchJob, jobParameters);

        //과거 etf 위클리 배치
//        LocalDate startDate = LocalDate.of(2024, 8, 5);
//        LocalDate endDate = LocalDate.of(2025, 8, 5);
//        LocalDate firstMonday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
//
//        for (LocalDate date = firstMonday; !date.isAfter(endDate); date = date.plusWeeks(1)) {
//            String dateStr = date.format(FORMATTER);
//            System.out.println("▶ 주차 기준 날짜: " + dateStr + " 배치 시작");
//
//            var params = new JobParametersBuilder()
//                    .addString("today", dateStr)
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//            jobLauncher.run(WeeklyBatchJob, params);
        }
    }



