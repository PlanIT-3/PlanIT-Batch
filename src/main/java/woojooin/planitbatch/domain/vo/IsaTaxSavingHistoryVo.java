package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
public class IsaTaxSavingHistoryVo {

	private Long memberId;
	private String quarter; // 예: "2024-Q1"
	private Long isaProfit; // ISA 수익
	private Long generalTax; // 일반계좌였다면 냈을 세금 = isaProfit * 0.154
	private Long taxSaved; // 절세 금액 = generalTax - 0 = generalTax

}

