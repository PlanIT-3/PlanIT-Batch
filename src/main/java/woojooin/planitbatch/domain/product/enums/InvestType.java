package woojooin.planitbatch.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvestType {
	SAFE("안전형"),
	CONSERVATIVE("안정추구형"),
	MODERATE("위험중립형"),
	AGGRESSIVE("적극투자형"),
	VERY_AGGRESSIVE("공격투자형");

	private final String korean;
}
