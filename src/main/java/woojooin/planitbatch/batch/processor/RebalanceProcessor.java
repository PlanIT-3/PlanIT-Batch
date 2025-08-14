package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

	@Override
	public Rebalance process(Balance balance) {
		if (balance == null || balance.getProduct() == null)
			return null;

		Product current = balance.getProduct();
		InvestType baseType = current.getInvestType() != null ? current.getInvestType() : InvestType.AGGRESSIVE;

		// 1) 타깃 상품 선정: 같은 성향에서 difference 최상위, 없으면 AGGRESSIVE
		Product target = productRepository
			.getHighestDifferenceProductByInvestType(baseType)
			.orElseGet(() -> productRepository.getHighestDifferenceProductByInvestType(InvestType.AGGRESSIVE)
				.orElse(null));

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
			.productCode(target.getShortenCode())                 // 다음에 갈 종목 코드
			.memberProductId(memberProductId)
			.goalId(goalId)
			.previousProductName(safe(current.getItemName()))
			.nextProductName(safe(target.getItemName()))
			.investType(target.getInvestType() != null ? target.getInvestType() : baseType)
			.expectedReturnRate(tgtExp)

			//Todo: comment는 openai 연동 예정
			.comment(String.format(
				"Rotate %s → %s | ΔExp=%s%%, ΔDiff=%+d",
				safe(current.getItemName()),
				safe(target.getItemName()),
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
