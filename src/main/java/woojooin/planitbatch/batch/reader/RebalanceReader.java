package woojooin.planitbatch.batch.reader;

import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;
import woojooin.planitbatch.domain.rebalance.vo.Balance;

@Component
@RequiredArgsConstructor
public class RebalanceReader implements ItemReader<Balance> {

	public static int CHUNK_SIZE = 10;
	private final JobLauncher jobLauncher;

	private int totalCount = 0;
	private int currentPage = 0;
	private int cursorInPage = 0;
	private List<Balance> currentList = Collections.emptyList();

	private final BalanceRepository balanceRepository;

	@Override
	public Balance read() {

		totalCount = balanceRepository.countAll();

		if (cursorInPage >= currentList.size()) {
			int offset = currentPage * CHUNK_SIZE;
			if (offset >= totalCount) {
				return null;
			}
			currentList = balanceRepository.findBalancePanging(offset, CHUNK_SIZE);
			currentPage++;
			cursorInPage = 0;
			if (currentList.isEmpty()) {
				return null;
			}
		}

		return currentList.get(cursorInPage++);
	}
}
