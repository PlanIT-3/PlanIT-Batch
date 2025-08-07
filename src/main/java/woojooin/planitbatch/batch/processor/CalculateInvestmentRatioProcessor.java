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

    private final MemberProductMapper MemberProductMapper;
    private final ProductMapper productMapper;

    @Override
    public List<InvestmentRatio> process(List<Member> members) throws Exception {

        List<Long> memberIds = members.stream() // Member의 id들을 List
            .map(Member::getMemberId)
            .collect(Collectors.toList());

        List<MemberProduct> allMemberProducts =
            MemberProductMapper.getMemberProductsByMemberIds(memberIds); // IN 절을 통해 MemberProduct 데이터 조회

        Map<String, Product> product = getProduct(allMemberProducts); // memberProduct를 통해 Product 데이터 조회

        Map<Long, List<MemberProduct>> memberProductMap =
            allMemberProducts.stream()
                .collect(Collectors.groupingBy(up -> up.getMemberId())); // memberId를 key로 해서 그룹핑

        return members.stream()
            .map(member -> calculateRatio(member.getMemberId(), // memberId와
                                        memberProductMap.get(member.getMemberId()), // 특정 memberId가 보유한 memberProduct 리스트와
                product)) // 특정 memberProductId의 Product 정보를 이용해서 계산
            .collect(Collectors.toList());
    }

    private Map<String, Product> getProduct(List<MemberProduct> allMemberProducts) {
        List<String> productIds = allMemberProducts.stream()
            .map(MemberProduct::getProductId)
            .distinct()
            .collect(Collectors.toList()); // MemberProduct 테이블에 productId가 중복으로 존재할테니 중복 제거 후 List 반환

        if (productIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Product> productMap = productMapper.getProductsByIds(productIds); // productId를 IN절을 이용하여 Product 데이터 조회

        return productMap;
    }

    private InvestmentRatio calculateRatio(Long memberId, List<MemberProduct> memberProducts, Map<String, Product> products) {
        log.info("=== Member {} 계산 시작 ===", memberId);

        if (memberProducts == null || memberProducts.isEmpty()) {
            log.info("Member {} - memberProducts가 비어있음", memberId);
            return createEmptyRatio(memberId);
        }

        log.info("Member {} - memberProducts 개수: {}", memberId, memberProducts.size());

        // 투자성향별 상품 개수 계산
        Map<String, Long> investTypeCounts = memberProducts.stream()
            .map(mp -> {
                Product product = products.get(mp.getProductId());
                log.info("ProductId: {} -> Product: {}", mp.getProductId(), product);
                return product;
            })
            .filter(product -> {
                boolean isValid = product != null && product.getInvestType() != null;
                if (!isValid) {
                    log.warn("유효하지 않은 Product: {}", product);
                }
                return isValid;
            })
            .collect(Collectors.groupingBy(
                product -> {
                    String investType = product.getInvestType().toString();
                    log.info("투자 타입: {}", investType);
                    return investType;
                },
                Collectors.counting()
            ));

        log.info("Member {} - 투자 타입 분포: {}", memberId, investTypeCounts);

        if (investTypeCounts.isEmpty()) {
            log.info("Member {} - investTypeCounts가 비어있음", memberId);
            return createEmptyRatio(memberId);
        }

        double totalCount = investTypeCounts.values().stream().mapToLong(Long::longValue).sum();
        log.info("Member {} - 총 상품 수: {}", memberId, totalCount);

        InvestmentRatio result = InvestmentRatio.builder()
            .memberId(memberId)
            .stable(calculatePercentage(investTypeCounts.getOrDefault("SAFE", 0L), totalCount))
            .income(calculatePercentage(investTypeCounts.getOrDefault("CONSERVATIVE", 0L), totalCount))
            .liquid(calculatePercentage(investTypeCounts.getOrDefault("MODERATE", 0L), totalCount))
            .growth(calculatePercentage(investTypeCounts.getOrDefault("AGGRESSIVE", 0L), totalCount))
            .diversified(calculatePercentage(investTypeCounts.getOrDefault("VERY_AGGRESSIVE", 0L), totalCount))
            .build();

        log.info("Member {} - 계산 결과: {}", memberId, result);
        log.info("=== Member {} 계산 완료 ===", memberId);

        return result;
    }
    private double calculatePercentage(long count, double total) {
        return total == 0 ? 0.0 : Math.round((count / total) * 100 * 100.0) / 100.0;
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