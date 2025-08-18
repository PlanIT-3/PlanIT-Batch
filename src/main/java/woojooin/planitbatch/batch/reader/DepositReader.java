package woojooin.planitbatch.batch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositVO;
import woojooin.planitbatch.global.service.DepositService;

@Slf4j
@Component
public class DepositReader implements ItemReader<DepositVO> {

	@Autowired
	private DepositService depositService;
	
	private Iterator<DepositVO> depositIterator;
	private volatile boolean isInitialized = false;
	private final Object initializationLock = new Object();

	@Override
	public DepositVO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		// 스레드 안전한 초기화 (Double-checked locking 패턴)
		if (!isInitialized) {
			synchronized (initializationLock) {
				if (!isInitialized) {
					initializeDepositData();
					isInitialized = true;
				}
			}
		}
		
		// Iterator에서 다음 데이터 반환 (synchronized 블록 외부에서 실행)
		if (depositIterator != null && depositIterator.hasNext()) {
			DepositVO deposit = depositIterator.next();
			log.debug("DepositReader에서 반환: {} ({}) - 잔액: {}", 
				deposit.getAccountName(), 
				deposit.getAccountNumber(), 
				deposit.getAccountBalance());
			return deposit;
		}
		
		// 더 이상 데이터가 없으면 null 반환 (배치 종료)
		return null;
	}
	
	private void initializeDepositData() {
		log.info("=== DB에서 예적금 계좌 정보 조회 시작 (계좌번호 'DEP'로 시작하는 것만) ===");
		
		try {
			// 데이터베이스에서 계좌번호가 'DEP'로 시작하는 예적금 계좌 정보만 조회
			List<DepositVO> deposits = depositService.getAllDeposits();
			
			if (deposits == null || deposits.isEmpty()) {
				log.warn("❌ DB에 'DEP'로 시작하는 예적금 계좌 데이터가 없습니다.");
				log.warn("   확인사항: account 테이블에 account_number가 'DEP'로 시작하는 데이터가 있는지 확인하세요.");
				depositIterator = List.<DepositVO>of().iterator();
				return;
			}
			
			log.info("✅ DB에서 조회된 예적금 계좌 수: {}", deposits.size());
			
			// 처음 몇 개 계좌 정보 출력 (디버깅용)
			for (int i = 0; i < Math.min(3, deposits.size()); i++) {
				DepositVO deposit = deposits.get(i);
				log.info("   계좌 {}: {} ({}) - 잔액: {}", 
					(i+1), deposit.getAccountName(), deposit.getAccountNumber(), deposit.getAccountBalance());
			}
			
			depositIterator = deposits.iterator();
			
		} catch (Exception e) {
			log.error("DB에서 예적금 계좌 정보 조회 중 오류 발생", e);
			throw new RuntimeException("예적금 계좌 정보 조회 실패", e);
		}
	}
} 