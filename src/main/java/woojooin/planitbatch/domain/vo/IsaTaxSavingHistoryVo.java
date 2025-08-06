package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
public class IsaTaxSavingHistoryVo {
	private Long memberId;
	private String quarter;
	private BigDecimal savingAmount;
	private BigDecimal accumulatedSaving;
	private LocalDateTime createdAt;
}

