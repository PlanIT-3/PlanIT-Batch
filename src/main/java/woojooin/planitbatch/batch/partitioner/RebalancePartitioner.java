package woojooin.planitbatch.batch.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;

@Component
@RequiredArgsConstructor
public class RebalancePartitioner implements Partitioner {
	private final BalanceRepository balanceRepository;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		int total = balanceRepository.countAll();
		int targetSize = (int)Math.ceil(total / (double)gridSize);

		Map<String, ExecutionContext> result = new HashMap<>();
		int start = 0;

		for (int i = 0; i < gridSize; i++) {
			int end = Math.min(start + targetSize, total);

			ExecutionContext context = new ExecutionContext();
			context.putInt("startOffset", start);
			context.putInt("endOffset", end);
			result.put("partition-" + i, context);

			start = end;
		}
		return result;
	}
}
