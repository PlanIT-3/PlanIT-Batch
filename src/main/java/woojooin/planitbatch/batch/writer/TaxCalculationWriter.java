package woojooin.planitbatch.batch.writer;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositTaxSavingVO;
import woojooin.planitbatch.global.service.TaxCalculationService;

@Slf4j
@Component
public class TaxCalculationWriter implements ItemWriter<DepositTaxSavingVO> {

	@Autowired
	private TaxCalculationService taxCalculationService;

	@Override
	public void write(List<? extends DepositTaxSavingVO> items) throws Exception {
		log.info("=== 세금 절약 내역 저장 시작 ===");
		
		try {
			log.info("저장할 세금 계산 건수: {}", items.size());

			// 생성 시간 설정 (배치 처리 전 일괄 설정)
			LocalDateTime now = LocalDateTime.now();
			for (DepositTaxSavingVO taxSaving : items) {
				if (taxSaving.getCreatedAt() == null) {
					taxSaving.setCreatedAt(now);
				}
			}

			// 기존: 개별 호출로 인한 커넥션 낭비
			// for (DepositTaxSavingVO taxSaving : items) {
			//     taxCalculationService.saveTaxCalculation(taxSaving);  // 1000번 개별 트랜잭션
			// }

			// 개선: 단일 트랜잭션으로 배치 처리 - 커넥션 재사용
			taxCalculationService.saveTaxCalculationBatch(new java.util.ArrayList<>(items));

			log.info("=== 세금 절약 내역 저장 완료 ===");

		} catch (Exception e) {
			log.error("세금 절약 내역 저장 중 오류 발생", e);
			throw e; // Spring Batch가 처리하도록 예외 전파
		}
	}
} 