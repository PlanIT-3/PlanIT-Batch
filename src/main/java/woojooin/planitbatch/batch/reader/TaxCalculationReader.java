package woojooin.planitbatch.batch.reader;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositVO;
import woojooin.planitbatch.global.service.DepositService;

@Slf4j
@Component
public class TaxCalculationReader implements ItemReader<DepositVO> {

	@Autowired
	private DepositService depositService;
	
	private List<DepositVO> deposits;
	private int currentIndex = 0;

	@Override
	public DepositVO read() throws Exception {
		// 첫 번째 호출 시 데이터 로드
		if (deposits == null) {
			deposits = depositService.getAllDeposits();
			log.info("세금 계산 대상 계좌 수: {}", deposits.size());
			currentIndex = 0;
		}
		
		// 모든 데이터를 읽었으면 null 반환 (배치 종료 신호)
		if (currentIndex >= deposits.size()) {
			return null;
		}
		
		// 현재 인덱스의 데이터 반환 후 인덱스 증가
		DepositVO deposit = deposits.get(currentIndex);
		currentIndex++;
		
		return deposit;
	}
} 