package woojooin.planitbatch.batch.writer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositVO;
import woojooin.planitbatch.global.service.DepositService;

@Slf4j
@Component
public class DepositWriter implements ItemWriter<DepositVO> {

	@Autowired
	private DepositService depositService;
	

	


	@Override
	public void write(List<? extends DepositVO> deposits) throws Exception {
		log.info("=== 예적금 계좌 정보 및 계산 결과 저장 시작 ===");

		try {
			log.info("저장할 계좌 수: {}", deposits.size());

			// 생성 시간 설정 (배치 처리 전 일괄 설정)
			LocalDateTime now = LocalDateTime.now();
			for (DepositVO account : deposits) {
				if (account.getCreatedAt() == null) {
					account.setCreatedAt(now);
				}
				if (account.getUpdatedAt() == null) {
					account.setUpdatedAt(now);
				}
			}

			// 기존: 개별 호출로 인한 커넥션 낭비
			// for (DepositVO account : deposits) {
			//     depositService.saveDeposit(account);  // 1000번 개별 트랜잭션
			// }

			// 개선: 단일 트랜잭션으로 배치 처리 - 커넥션 재사용
			depositService.saveDepositBatch(new java.util.ArrayList<>(deposits));
			

			


			log.info("=== 예적금 계좌 정보 및 계산 결과 저장 완료 ===");

		} catch (Exception e) {
			log.error("예적금 계좌 정보 및 계산 결과 저장 중 오류 발생", e);
			throw e; // Spring Batch가 처리하도록 예외 전파
		}
	}
} 