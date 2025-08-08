package woojooin.planitbatch.domain.rebalance.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import woojooin.planitbatch.domain.product.enums.InvestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rebalance {
	private Long rebalanceId;
	private String productCode;
	private Long memberProductId;
	private Long goalId;
	private String comment;
	private BigDecimal expectedReturnRate;
	private String previousProductName;
	private String nextProductName;
	private InvestType investType;
}
