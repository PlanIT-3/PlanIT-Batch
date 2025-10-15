package woojooin.planitbatch.batch.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;

@Component
@RequiredArgsConstructor
public class RebalancePartitioner implements Partitioner {
	private final BalanceRepository balanceRepository;

	@Qualifier("batchTaskExecutor")
	private final ThreadPoolTaskExecutor batchTaskExecutor;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		System.out.println("=== [RebalancePartitioner] Executor Info ===");
		System.out.println("ThreadNamePrefix : " + batchTaskExecutor.getThreadNamePrefix());
		System.out.println("CorePoolSize     : " + batchTaskExecutor.getCorePoolSize());
		System.out.println("MaxPoolSize      : " + batchTaskExecutor.getMaxPoolSize());
		System.out.println(
			"QueueCapacity    : " + batchTaskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity());
		System.out.println("===========================================");

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
