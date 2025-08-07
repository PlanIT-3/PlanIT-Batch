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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CalculateInvestmentRatioScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("memberInvestmentRatioJob")
    private Job investmentRatioJob;

    @Scheduled(cron = "0 44 15 * * ?")
    public void runInvestmentRatioJob() {
        try {
            log.info("투자성향 비율 계산 배치 작업 시작");
            
            String dateParam = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", dateParam)
                .toJobParameters();
            
            jobLauncher.run(investmentRatioJob, jobParameters);
            
            log.info("투자성향 비율 계산 배치 작업 완료");
        } catch (Exception e) {
            log.error("투자성향 비율 계산 배치 작업 실패", e);
        }
    }
}