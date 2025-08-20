package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.product.enums.InvestType;
import woojooin.planitbatch.domain.product.repository.ProductRepository;
import woojooin.planitbatch.domain.product.vo.Product;
import woojooin.planitbatch.domain.rebalance.vo.Balance;
import woojooin.planitbatch.domain.rebalance.vo.Rebalance;

@Component
@RequiredArgsConstructor
public class RebalanceProcessor implements ItemProcessor<Balance, Rebalance> {

	private final ProductRepository productRepository;

	private static long totalElapsed = 0;
	private static long count = 0;
	private Map<InvestType, Product> products = new HashMap<>();

	@Override
	public Rebalance process(Balance balance) {
		if (balance == null || balance.getProduct() == null)
			return null;

		Product current = balance.getProduct();
		InvestType baseType = current.getInvestType() != null ? current.getInvestType() : InvestType.AGGRESSIVE;

		long start = System.nanoTime();

		Product target;

		if (products.containsKey(baseType)) {
			target = products.get(baseType);
		} else {
			// 1) 타깃 상품 선정: 같은 성향에서 difference 최상위, 없으면 AGGRESSIVE
			target = productRepository
				.getHighestDifferenceProductByInvestType(baseType)
				.orElseGet(() -> productRepository.getHighestDifferenceProductByInvestType(InvestType.AGGRESSIVE)
					.orElse(null));
		}

		long end = System.nanoTime();
		long elapsedTime = end - start;

		synchronized (RebalanceProcessor.class) {
			totalElapsed += elapsedTime;
			count++;
		}

		if (target == null)
			return null;

		// 동일 종목이면 rebalance 없음
		if (Objects.equals(target.getShortenCode(), current.getShortenCode())) {
			return null;
		}

		// 지표 계산
		BigDecimal currExp = nz(current.getExpectedReturnRate());
		BigDecimal tgtExp = nz(target.getExpectedReturnRate());
		BigDecimal expGap = tgtExp.subtract(currExp).setScale(2, RoundingMode.HALF_UP);

		int currDiff = current.getDifference() != null ? current.getDifference() : 0;
		int tgtDiff = target.getDifference() != null ? target.getDifference() : 0;
		int diffGap = tgtDiff - currDiff;

		Long memberProductId = balance.getMember_product() != null
			? balance.getMember_product().getMemberProductId()
			: null;
		Long goalId = balance.getGoalId();

		// 5) Rebalance 빌드
		return Rebalance.builder()
			.productCode(target.getShortenCode())
			.memberProductId(memberProductId)
			.goalId(goalId)
			.previousProductName(safe(current.getItemName()))
			.nextProductName(safe(target.getItemName()))
			.investType(target.getInvestType() != null ? target.getInvestType() : baseType)
			.expectedReturnRate(tgtExp)

			.comment(String.format(
				"만약 이 상품으로 교체했다면,\n수익이 %s%%p 높아지고\n구조 지표는 %+d만큼 개선됐을 거예요.",
				expGap.toPlainString(),
				diffGap))
			.build();
	}

	private static BigDecimal nz(BigDecimal v) {
		return v != null ? v : BigDecimal.ZERO;
	}

	private static String safe(String s) {
		return s != null ? s : "";
	}

}
