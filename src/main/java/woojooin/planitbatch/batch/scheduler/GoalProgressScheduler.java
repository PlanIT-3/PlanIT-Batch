package woojooin.planitbatch.batch.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
public class GoalProgressScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("dailyGoalProgressJob")
    private Job goalProgressJob;

    @Scheduled(cron = "0 37 10 * * ?")
    public void runGoalProgressJob() {
        try {
            log.info("목표 진행률 계산 배치 작업 시작");
            
            String dateParam = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", dateParam)
                .toJobParameters();
            
            jobLauncher.run(goalProgressJob, jobParameters);
            
            log.info("목표 진행률 계산 배치 작업 완료");
        } catch (Exception e) {
            log.error("목표 진행률 계산 배치 작업 실패", e);
        }
    }
}