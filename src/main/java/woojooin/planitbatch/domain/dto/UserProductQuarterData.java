package woojooin.planitbatch.domain.dto;


import java.math.BigDecimal;
import lombok.Data;

@Data
public class UserProductQuarterData {
	private Long memberId;
	private String quarter;
	private BigDecimal totalProfit;
}
