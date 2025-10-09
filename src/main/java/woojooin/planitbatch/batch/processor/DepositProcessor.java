package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositVO;

@Slf4j
@Component
public class DepositProcessor implements ItemProcessor<DepositVO, DepositVO> {

	@Override
	public DepositVO process(DepositVO deposit) throws Exception {
		log.info("🔥 DepositProcessor 처리 중: {} ({}) - 잔액: {}", 
			deposit.getAccountName(), 
			deposit.getAccountNumber(), 
			deposit.getAccountBalance());
		
		// 유효성 검사: 잔액이 0보다 작거나 같으면 null 반환 (제외)
		if (deposit.getAccountBalance() == null || 
			deposit.getAccountBalance().compareTo(BigDecimal.ZERO) <= 0) {
			log.warn("유효하지 않은 계좌 제외: {} - 잔액: {}", 
				deposit.getAccountNumber(), deposit.getAccountBalance());
			return null; // null 반환시 해당 아이템은 Writer로 전달되지 않음
		}
		
		return processDepositAccount(deposit);
	}

	/**
	 * 개별 예적금 계좌 정보를 가공합니다.
	 */
	private DepositVO processDepositAccount(DepositVO deposit) {
		try {
			// 1. 기본 정보 설정
			if (deposit.getCreatedAt() == null) {
				deposit.setCreatedAt(LocalDateTime.now());
			}
			deposit.setUpdatedAt(LocalDateTime.now());
			deposit.setIsDeleted(false);
			deposit.setIsIntegrated(true);

			// 2. 이자 계산 (earnings_rate는 이미 소수점 형태, 양수만 처리)
			// 성능 최적화를 위해 debug 로그 제거
			
			// 임시로 조건을 단순화해서 문제 원인 파악
			if (deposit.getAccountBalance() != null && deposit.getAccountBalance().compareTo(BigDecimal.ZERO) > 0) {
				
				// earnings_rate가 null이면 0.03(3%)로 기본값 설정
				BigDecimal earningsRate = deposit.getEarningsRate();
				if (earningsRate == null || earningsRate.compareTo(BigDecimal.ZERO) <= 0) {
					earningsRate = new BigDecimal("0.03"); // 기본 3% 이자율
				}
				
				BigDecimal interestAmount = deposit.getAccountBalance()
					.multiply(earningsRate)
					.setScale(2, RoundingMode.HALF_UP);
				deposit.setInterestAmount(interestAmount);
				
				// 3. 이자 금액만 설정 (세금 계산은 별도 배치에서 처리)
				log.info("✅ 이자 계산 완료: 계좌={}, 이자금액={}", deposit.getAccountNumber(), interestAmount);
			}

			log.debug("예적금 계좌 가공 완료: {} ({})", deposit.getAccountName(), deposit.getAccountNumber());

			return deposit;

		} catch (Exception e) {
			log.error("예적금 계좌 가공 중 오류 발생: {}", deposit.getAccountNumber(), e);
			return deposit; // 오류가 발생해도 원본 데이터 반환
		}
	}
	

} 