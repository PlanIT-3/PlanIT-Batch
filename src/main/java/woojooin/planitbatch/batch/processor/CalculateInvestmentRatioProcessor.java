package woojooin.planitbatch.batch.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.MemberProductMapper;
import woojooin.planitbatch.domain.product.mapper.ProductMapper;
import woojooin.planitbatch.domain.product.vo.Product;
import woojooin.planitbatch.domain.vo.InvestmentRatio;
import woojooin.planitbatch.domain.vo.Member;
import woojooin.planitbatch.domain.vo.MemberProduct;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class CalculateInvestmentRatioProcessor implements ItemProcessor<List<Member>, List<InvestmentRatio>> {

	private final MemberProductMapper memberProductMapper;
	private final ProductMapper productMapper;

	@Override
	public List<InvestmentRatio> process(List<Member> members) throws Exception {

		List<Long> memberIds = members.stream()
			.map(Member::getMemberId)
			.collect(Collectors.toList());

		// 1. 각 회원의 투자 상품 조회
		List<MemberProduct> allMemberProducts =
			memberProductMapper.getMemberProductsByMemberIds(memberIds);

		// 2. shortenCode 기준으로 Product 조회
		Map<String, Product> productMap = getProductMap(allMemberProducts);

		// 3. memberId별 보유 상품 그룹화
		Map<Long, List<MemberProduct>> memberProductMap =
			allMemberProducts.stream()
				.collect(Collectors.groupingBy(MemberProduct::getMemberId));

		// 4. 각 회원별 비율 계산
		return members.stream()
			.map(member -> calculateRatio(
				member.getMemberId(),
				memberProductMap.get(member.getMemberId()),
				productMap))
			.collect(Collectors.toList());
	}

	private Map<String, Product> getProductMap(List<MemberProduct> allMemberProducts) {
		List<String> shortenCodes = allMemberProducts.stream()
			.map(MemberProduct::getShortenCode)
			.distinct()
			.collect(Collectors.toList());

		if (shortenCodes.isEmpty()) {
			return new HashMap<>();
		}

		// shortenCode를 IN 절로 조회
		return productMapper.getProductsByShortenCodes(shortenCodes);
	}

	private InvestmentRatio calculateRatio(Long memberId, List<MemberProduct> memberProducts,
		Map<String, Product> products) {

		if (memberProducts == null || memberProducts.isEmpty()) {
			log.info("Member {} - memberProducts가 비어있음", memberId);
			return createEmptyRatio(memberId);
		}

		// 투자성향별 상품 개수 계산
		Map<String, Long> investTypeCounts = memberProducts.stream()
			.map(mp -> products.get(mp.getShortenCode()))
			.filter(product -> product != null && product.getInvestType() != null)
			.collect(Collectors.groupingBy(
				product -> product.getInvestType().toString(),
				Collectors.counting()
			));

		if (investTypeCounts.isEmpty()) {
			log.info("Member {} - investTypeCounts가 비어있음", memberId);
			return createEmptyRatio(memberId);
		}

		double totalCount = investTypeCounts.values().stream()
			.mapToLong(Long::longValue)
			.sum();

		return InvestmentRatio.builder()
			.memberId(memberId)
			.stable(calculatePercentage(investTypeCounts.getOrDefault("SAFE", 0L), totalCount))
			.income(calculatePercentage(investTypeCounts.getOrDefault("CONSERVATIVE", 0L), totalCount))
			.liquid(calculatePercentage(investTypeCounts.getOrDefault("MODERATE", 0L), totalCount))
			.growth(calculatePercentage(investTypeCounts.getOrDefault("AGGRESSIVE", 0L), totalCount))
			.diversified(calculatePercentage(investTypeCounts.getOrDefault("VERY_AGGRESSIVE", 0L), totalCount))
			.build();
	}

	private double calculatePercentage(long count, double total) {
		return total == 0 ? 0.0 : Math.round((count / total) * 10000.0) / 100.0;
	}

	private InvestmentRatio createEmptyRatio(Long memberId) {
		return InvestmentRatio.builder()
			.memberId(memberId)
			.stable(0.0)
			.income(0.0)
			.liquid(0.0)
			.growth(0.0)
			.diversified(0.0)
			.build();
	}
}
