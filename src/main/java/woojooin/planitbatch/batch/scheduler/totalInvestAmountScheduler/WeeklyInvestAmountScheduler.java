package woojooin.planitbatch.batch.scheduler.totalInvestAmountScheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class WeeklyInvestAmountScheduler {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("weeklyInvestJob")
    private Job weeklyInvestJob;

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 자정에 실행
    public  void runWeeklyInvestmentJob() {
        try {
            log.info("주간 투자금액 계산 배치 작업 시작");
            String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", targetDate)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("배치 작업 시작 - targetDate: {}", targetDate);

            // Job 실행
            jobLauncher.run(weeklyInvestJob, jobParameters);

            log.info("주간 투자금액 계산 배치 작업 완료");
        } catch (Exception e) {
            log.error("주간 투자금액 계산 배치 작업 실패", e);
        }
    }
}
