package woojooin.planitbatch.batch.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.batch.job.DepositJobService;
import woojooin.planitbatch.batch.job.TaxCalculationJobService;

@Slf4j
@Component
public class JobScheduler {

    @Autowired 
    private DepositJobService depositJobService; // Chunk 방식 예적금 배치 서비스 (DB 기반)
    
    @Autowired
    private TaxCalculationJobService taxCalculationJobService; // 세금 계산 배치 서비스
    
    // 배치 실행 상태 추적을 위한 플래그
    private volatile boolean isDepositJobRunning = false;
    private volatile boolean isTaxJobRunning = false;
    private final Object jobLock = new Object();

    @Scheduled(cron = "0 51 14 * * ?") // 테스트용: 14시 51분 실행
    public void runDepositCollectionJob() {
        synchronized (jobLock) {  // 동시 실행 방지
            if (isDepositJobRunning) {
                log.warn("예적금 배치가 이미 실행 중입니다. 스킵합니다.");
                return;
            }
            isDepositJobRunning = true;
        }
        
        try {
            log.info("=== DB 방식 예적금 배치 실행 시작 ===");
            depositJobService.executeDepositCollectionJob();
            log.info("=== DB 방식 예적금 배치 실행 완료 ===");
            
        } catch (Exception e) {
            log.error("=== DB 방식 예적금 배치 실행 실패 ===", e);
        } finally {
            isDepositJobRunning = false;  // 상태 초기화
        }
    }
    
    @Scheduled(cron = "0 52 14 * * ?") // 테스트용: 14시 52분 실행
    public void runTaxCalculationJob() {
        synchronized (jobLock) {
            // 예적금 배치가 실행 중이면 대기
            if (isDepositJobRunning) {
                log.warn("예적금 배치 실행 중이므로 세금 계산 배치를 지연합니다.");
                try {
                    Thread.sleep(60000); // 1분 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            
            if (isTaxJobRunning) {
                log.warn("세금 계산 배치가 이미 실행 중입니다. 스킵합니다.");
                return;
            }
            isTaxJobRunning = true;
        }
        
        try {
            log.info("=== 세금 계산 배치 실행 시작 ===");
            taxCalculationJobService.executeTaxCalculationJob();
            log.info("=== 세금 계산 배치 실행 완료 ===");
        } catch (Exception e) {
            log.error("=== 세금 계산 배치 실행 실패 ===", e);
        } finally {
            isTaxJobRunning = false;
        }
    }
    
    /**
     * 수동 실행 메서드들 - 동시 실행 방지 로직 적용
     */
    
    public void runDbBatch() {
        synchronized (jobLock) {
            if (isDepositJobRunning || isTaxJobRunning) {
                log.warn("다른 배치가 실행 중입니다. 수동 배치를 스킵합니다.");
                return;
            }
            isDepositJobRunning = true;
        }
        
        try {
            log.info("=== 수동 예적금 계좌 수집 배치 실행 시작 ===");
            depositJobService.executeDepositCollectionJob();
            log.info("=== 수동 예적금 계좌 수집 배치 실행 완료 ===");
            
            // 예적금 수집 완료 후 세금 계산 배치 실행
            synchronized (jobLock) {
                isDepositJobRunning = false;
                isTaxJobRunning = true;
            }
            
            log.info("=== 수동 세금 계산 배치 실행 시작 ===");
            taxCalculationJobService.executeTaxCalculationJob();
            log.info("=== 수동 세금 계산 배치 실행 완료 ===");
            
        } catch (Exception e) {
            log.error("=== 수동 배치 실행 실패 ===", e);
        } finally {
            isDepositJobRunning = false;
            isTaxJobRunning = false;
        }
    }
    
    /**
     * 세금 계산 전용 배치 실행 - 동시 실행 방지 로직 적용
     */
    public void runTaxCalculationBatch() {
        synchronized (jobLock) {
            if (isTaxJobRunning || isDepositJobRunning) {
                log.warn("다른 배치가 실행 중입니다. 세금 계산 배치를 스킵합니다.");
                return;
            }
            isTaxJobRunning = true;
        }
        
        try {
            log.info("=== 수동 세금 계산 배치 실행 시작 ===");
            taxCalculationJobService.executeTaxCalculationJob();
            log.info("=== 수동 세금 계산 배치 실행 완료 ===");
        } catch (Exception e) {
            log.error("=== 수동 세금 계산 배치 실행 실패 ===", e);
        } finally {
            isTaxJobRunning = false;
        }
    }
    
}