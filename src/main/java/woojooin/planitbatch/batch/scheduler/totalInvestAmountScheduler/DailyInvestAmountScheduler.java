package woojooin.planitbatch.batch.scheduler.totalInvestAmountScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyInvestAmountScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("dailyInvestJob")
    private Job dailyInvestJob;


    @Scheduled(cron = "0 44 15 * * ?")
    public void runDailyInvestmentJob()
    {
        try {
            log.info("일간 투자금액 계산 배치 작업 시작");

            String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            JobParameters jobParameters = new JobParametersBuilder()
                            .addString("targetDate", targetDate)
                            .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

                    log.info("배치 작업 시작 - targetDate:{}", targetDate);
            // Job 실행
            jobLauncher.run(dailyInvestJob, jobParameters);

            log.info("일간 투자금액 계산 배치 작업 완료");
        } catch (Exception e) {
            log.error("일간 투자금액  계산 배치 작업 실패", e);
        }
    }
}