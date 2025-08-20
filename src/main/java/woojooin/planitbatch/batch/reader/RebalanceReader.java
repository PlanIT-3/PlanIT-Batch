package woojooin.planitbatch.batch.reader;

import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;
import woojooin.planitbatch.domain.rebalance.vo.Balance;

@Component
@StepScope
@RequiredArgsConstructor
public class RebalanceReader implements ItemReader<Balance> {

	private final BalanceRepository balanceRepository;

	@Value("#{stepExecutionContext['startOffset']}")
	private Integer startOffset;

	@Value("#{stepExecutionContext['endOffset']}")
	private Integer endOffset;

	private int nextOffset = -1;
	private int cursorInPage = 0;
	private List<Balance> currentList = Collections.emptyList();
	public static int CHUNK_SIZE = 10;

	@Override
	public Balance read() {
		if (nextOffset == -1) {
			nextOffset = startOffset;
		}

		if (nextOffset >= endOffset) {
			return null; // 내 파티션 범위 끝
		}

		// 현재 페이지 다 읽었으면 새로 로드
		if (cursorInPage >= currentList.size()) {
			int remaining = endOffset - nextOffset;
			int pageSize = Math.min(RebalanceReader.CHUNK_SIZE, remaining);

			currentList = balanceRepository.findBalancePanging(nextOffset, pageSize);
			cursorInPage = 0;

			if (currentList.isEmpty()) {
				return null;
			}
		}

		Balance item = currentList.get(cursorInPage++);
		if (cursorInPage >= currentList.size()) {
			nextOffset += currentList.size();
		}
		return item;
	}
}