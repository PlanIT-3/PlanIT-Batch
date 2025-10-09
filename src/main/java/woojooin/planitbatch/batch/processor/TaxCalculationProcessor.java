package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositTaxSavingVO;
import woojooin.planitbatch.domain.vo.DepositVO;

@Slf4j
@Component
public class TaxCalculationProcessor implements ItemProcessor<DepositVO, DepositTaxSavingVO> {

	@Override
	public DepositTaxSavingVO process(DepositVO deposit) throws Exception {
		try {
			// 1. 이자 계산
			BigDecimal interestAmount = calculateInterest(deposit);
			
			// 2. 세금 절약 내역 VO 생성
			DepositTaxSavingVO taxSaving = createDepositTaxSaving(deposit, interestAmount);
			
			// 3. 세금 계산 실행
			taxSaving.calculateTax();
			
			log.debug("세금 계산 완료: 계좌={}, 이자={}, 총세금={}", 
				deposit.getAccountNumber(), interestAmount, taxSaving.getTotalTax());
			
			return taxSaving;
			
		} catch (Exception e) {
			log.error("계좌 세금 계산 중 오류 발생: {}", deposit.getAccountNumber(), e);
			return null; // 오류 발생 시 해당 아이템 스킵
		}
	}
	
	/**
	 * 이자 금액을 계산합니다.
	 */
	private BigDecimal calculateInterest(DepositVO deposit) {
		if (deposit.getAccountBalance() == null || deposit.getAccountBalance().compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		
		BigDecimal earningsRate = deposit.getEarningsRate();
		if (earningsRate == null || earningsRate.compareTo(BigDecimal.ZERO) <= 0) {
			earningsRate = new BigDecimal("0.03"); // 기본 3% 이자율
		}
		
		return deposit.getAccountBalance()
			.multiply(earningsRate)
			.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 세금 절약 내역 VO를 생성합니다.
	 */
	private DepositTaxSavingVO createDepositTaxSaving(DepositVO deposit, BigDecimal interestAmount) {
		return DepositTaxSavingVO.builder()
			.memberId(deposit.getMemberId())
			.accountId(deposit.getAccountId())
			.quarter(DepositTaxSavingVO.getCurrentQuarter())
			.interestIncome(interestAmount)
			.createdAt(LocalDateTime.now())
			.build();
	}
} 