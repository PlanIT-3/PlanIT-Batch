package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.product.repository.EtfDailyHistoryRepository;
import woojooin.planitbatch.domain.product.repository.ProductRepository;
import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;
import woojooin.planitbatch.domain.product.vo.Product;
import woojooin.planitbatch.global.util.calculate.RebalanceCalc;

@Component
@RequiredArgsConstructor
public class ProductProcessor implements ItemProcessor<Product, Product> {

	private final ProductRepository productRepository;
	private final EtfDailyHistoryRepository etfDailyHistoryRepository;

	private final Integer PREDICT_MONTH = 1;

	/**
	 * 1 달뒤 예상 증가량 구하기
	 * @param product to be processed, never {@code null}.
	 * @return
	 * @throws Exception
	 */
	@Override
	public Product process(Product product) throws Exception {

		LocalDate start = LocalDate.now().minusMonths(PREDICT_MONTH);
		List<EtfDailyHistory> histories = etfDailyHistoryRepository.findByProductAfterStart(product.getShortenCode(),
			start);
		BigDecimal expectedReturnRate = RebalanceCalc.predictReturnPercentageMonths(histories, PREDICT_MONTH);

		product.setExpectedReturnRate(expectedReturnRate);

		return product;
	}
}
