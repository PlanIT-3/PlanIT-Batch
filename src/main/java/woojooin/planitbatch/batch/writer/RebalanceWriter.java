package woojooin.planitbatch.batch.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.rebalance.repository.RebalanceRepository;
import woojooin.planitbatch.domain.rebalance.vo.Rebalance;

@Slf4j
@Component
@RequiredArgsConstructor
public class RebalanceWriter implements ItemWriter<Rebalance> {

	private final RebalanceRepository rebalanceRepository;

	@Override
	public void write(List<? extends Rebalance> items) throws Exception {

		List<Rebalance> rebalances = new ArrayList<>();

		for (Rebalance rebalance : items) {
			rebalances.add(rebalance);
		}

		log.info("[writer] - {}", rebalances);

		rebalanceRepository.insertAll(rebalances);
	}
}
