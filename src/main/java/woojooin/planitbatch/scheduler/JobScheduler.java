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

    // === 예적금 세금 계산 Job ===
    @Autowired
    private Job depositTaxCalculationJob;

    /**
     * 예적금 세금 계산 배치 실행
     * 매일 새벽 2시 실행
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDepositTaxCalculationJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("jobName", "depositTaxCalculation")
                .toJobParameters();
            
            jobLauncher.run(depositTaxCalculationJob, jobParameters);
            
        } catch (Exception e) {
            System.err.println("Failed to execute deposit tax calculation job: " + e.getMessage());
            e.printStackTrace();
        }
    }
}