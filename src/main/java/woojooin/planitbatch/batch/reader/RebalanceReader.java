package woojooin.planitbatch.batch.reader;

import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.rebalance.repository.BalanceRepository;
import woojooin.planitbatch.domain.rebalance.vo.Balance;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class RebalanceReader implements ItemReader<Balance> {

	private final BalanceRepository balanceRepository;

	@Value("#{stepExecutionContext['startGoalId']}")
	private long startGoalId;

	@Value("#{stepExecutionContext['endGoalId']}")
	private long endGoalId;

	public static final int CHUNK_SIZE = 100;

	private List<Balance> buffer = Collections.emptyList();
	private int cursorInChunk = 0;
	private long lastGoalId = -1;
	private boolean finished = false;

	@Override
	public Balance read() {
		// 이미 끝났으면 null
		if (finished)
			return null;

		// 현재 chunk 소진 시 다음 chunk 로드
		if (cursorInChunk >= buffer.size()) {
			buffer = loadNextChunk();
			cursorInChunk = 0;

			if (buffer.isEmpty()) {
				finished = true;
				return null;
			}
		}

		Balance item = buffer.get(cursorInChunk++);
		lastGoalId = item.getGoalId();
		return item;
	}

	private List<Balance> loadNextChunk() {
		long startId = (lastGoalId == -1) ? startGoalId : lastGoalId + 1;
		List<Balance> chunk = balanceRepository.findBalanceChunk(startId, endGoalId, cursorInChunk, CHUNK_SIZE);
		return chunk;
	}
}

// @Component
// @RequiredArgsConstructor
// public class RebalanceReader implements ItemReader<Balance> {
//
// 	private final BalanceRepository balanceRepository;
//
// 	private int nextOffset = 0;
// 	private int cursorInPage = 0;
// 	private List<Balance> currentList = Collections.emptyList();
// 	public static int CHUNK_SIZE = 50;
// 	private static final int MAX_LIMIT = 200000; // ✅ 전체 범위
//
// 	@Override
// 	public Balance read() {
// 		// 현재 페이지 데이터를 모두 읽었다면 다음 페이지 로드
// 		if (cursorInPage >= currentList.size()) {
// 			if (nextOffset >= MAX_LIMIT) {
// 				return null; // 전체 범위 끝
// 			}
//
// 			int remaining = MAX_LIMIT - nextOffset;
// 			int pageSize = Math.min(CHUNK_SIZE, remaining);
//
// 			currentList = balanceRepository.findBalancePanging(nextOffset, pageSize);
// 			cursorInPage = 0;
//
// 			if (currentList.isEmpty()) { // 더 이상 데이터 없으면
// 				return null;
// 			}
// 		}
//
// 		Balance item = currentList.get(cursorInPage++);
// 		if (cursorInPage >= currentList.size()) {
// 			nextOffset += currentList.size();
// 		}
// 		return item;
// 	}
// }
//
// @Component
// @StepScope
// @RequiredArgsConstructor
// public class RebalanceReader implements ItemReader<Balance> {
//
// 	private final BalanceRepository balanceRepository;
//
// 	@Value("#{stepExecutionContext['startOffset']}")
// 	private Integer startOffset;
//
// 	@Value("#{stepExecutionContext['endOffset']}")
// 	private Integer endOffset;
//
// 	private int nextOffset = -1;
// 	private int cursorInPage = 0;
// 	private List<Balance> currentList = Collections.emptyList();
// 	public static int CHUNK_SIZE = 50;
//
// 	@Override
// 	public Balance read() {
// 		if (nextOffset == -1) {
// 			nextOffset = startOffset;
// 		}
//
// 		if (nextOffset >= endOffset) {
// 			return null; // 내 파티션 범위 끝
// 		}
//
// 		// 현재 페이지 다 읽었으면 새로 로드
// 		if (cursorInPage >= currentList.size()) {
// 			int remaining = endOffset - nextOffset;
// 			int pageSize = Math.min(RebalanceReader.CHUNK_SIZE, remaining);
//
// 			currentList = balanceRepository.findBalancePanging(nextOffset, pageSize);
// 			cursorInPage = 0;
//
// 			if (currentList.isEmpty()) {
// 				return null;
// 			}
// 		}
//
// 		Balance item = currentList.get(cursorInPage++);
// 		if (cursorInPage >= currentList.size()) {
// 			nextOffset += currentList.size();
// 		}
// 		return item;
// 	}
// }