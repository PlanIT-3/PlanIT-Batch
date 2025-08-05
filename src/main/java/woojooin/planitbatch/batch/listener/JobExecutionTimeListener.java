package woojooin.planitbatch.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobExecutionTimeListener implements JobExecutionListener {

    private long startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("Job {} started at {}", jobExecution.getJobInstance().getJobName(), 
                new java.util.Date(startTime));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        log.info("Job {} finished at {} with status: {}", 
                jobExecution.getJobInstance().getJobName(),
                new java.util.Date(endTime),
                jobExecution.getStatus());
        
        log.info("Job {} execution time: {} ms ({} seconds)", 
                jobExecution.getJobInstance().getJobName(),
                executionTime,
                executionTime / 1000.0);
    }
}