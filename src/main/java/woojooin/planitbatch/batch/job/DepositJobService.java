package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepositJobService {

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job depositCollectionJob; // BatchConfig에서 정의한 chunk 방식 Job
	
	// 커넥션 풀 모니터링을 위한 HikariDataSource 주입
	@Autowired
	@Qualifier("dataSource")
	private HikariDataSource hikariDataSource;

	/**
	 * 예적금 계좌 정보 수집 및 처리 Job을 chunk 1000개 방식으로 실행합니다.
	 */
	public void executeDepositCollectionJob() {
		// 배치 실행 전 커넥션 풀 상태 로깅
		logConnectionPoolStatus("배치 실행 전");
		
		log.info("=== 예적금 계좌 정보 수집 Job 시작 (Chunk 1000개 방식) ===");
		
		try {
			// Spring Batch JobLauncher를 사용해서 chunk 방식 Job 실행
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.addString("jobName", "depositCollectionJob")
				.toJobParameters();
			
			JobExecution jobExecution = jobLauncher.run(depositCollectionJob, jobParameters);
			
			// 배치 실행 결과 상세 로깅
			BatchStatus status = jobExecution.getStatus();
			log.info("배치 실행 결과: {}", status);
			log.info("배치 시작 시간: {}", jobExecution.getStartTime());
			log.info("배치 종료 시간: {}", jobExecution.getEndTime());
			
			if (status == BatchStatus.FAILED) {
				log.error("배치 실행 실패. 실패 예외: {}", jobExecution.getAllFailureExceptions());
				throw new RuntimeException("배치 실행 실패");
			}
			
			log.info("=== 예적금 계좌 정보 수집 Job 완료 (Chunk 1000개 방식) ===");
			
		} catch (Exception e) {
			log.error("예적금 계좌 정보 수집 Job 실행 중 오류 발생", e);
			
			// 배치 실행 후 커넥션 풀 상태 확인 (누수 탐지)
			logConnectionPoolStatus("배치 실행 실패 후");
			
			// 커넥션 풀 상태가 비정상이면 강제 정리
			cleanupConnectionPoolIfNeeded();
			
			throw new RuntimeException("Chunk Job 실행 실패", e);
		} finally {
			// 배치 실행 완료 후 커넥션 풀 상태 로깅
			logConnectionPoolStatus("배치 실행 완료 후");
		}
	}
	
	/**
	 * 커넥션 풀 상태 로깅
	 */
	private void logConnectionPoolStatus(String phase) {
		try {
			HikariPoolMXBean poolBean = hikariDataSource.getHikariPoolMXBean();
			log.info("=== 커넥션 풀 상태 ({}) ===", phase);
			log.info("활성 커넥션: {}/{}", poolBean.getActiveConnections(), poolBean.getTotalConnections());
			log.info("유휴 커넥션: {}", poolBean.getIdleConnections());
			log.info("대기 중인 스레드: {}", poolBean.getThreadsAwaitingConnection());
		} catch (Exception e) {
			log.warn("커넥션 풀 상태 조회 실패", e);
		}
	}
	
	/**
	 * 커넥션 풀 정리 (필요시)
	 */
	private void cleanupConnectionPoolIfNeeded() {
		try {
			HikariPoolMXBean poolBean = hikariDataSource.getHikariPoolMXBean();
			int activeConnections = poolBean.getActiveConnections();
			int totalConnections = poolBean.getTotalConnections();
			
			// 활성 커넥션이 전체의 80% 이상이면 의심스러운 상황
			if (activeConnections > totalConnections * 0.8) {
				log.warn("커넥션 누수 의심: 활성 커넥션 {}/{}", activeConnections, totalConnections);
				
				// HikariCP의 소프트 에빅션 수행 (유휴 커넥션 정리)
				poolBean.softEvictConnections();
				log.info("커넥션 풀 소프트 에빅션 수행 완료");
			}
			
		} catch (Exception e) {
			log.error("커넥션 풀 정리 중 오류 발생", e);
		}
	}
}
