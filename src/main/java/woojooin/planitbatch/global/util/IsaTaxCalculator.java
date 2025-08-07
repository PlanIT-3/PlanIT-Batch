package woojooin.planitbatch.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

public class IsaTaxCalculator {

	/**
	 * ISA 절세 효과를 계산해서 IsaTaxSavingHistoryVo에 맞는 값을 세팅해 반환한다.
	 *
	 * @param memberId 회원 ID
	 * @param quarter  분기 (예: "2024-Q1")
	 * @param totalProfit ISA 계좌 전체 수익
	 * @param userType  "general" 또는 "preferential"
	 * @return IsaTaxSavingHistoryVo (isaProfit, generalTax, taxSaved 세팅 완료)
	 */
	public static IsaTaxSavingHistoryVo calculateIsaTaxSavingHistoryVo(Long memberId, String quarter, BigDecimal totalProfit, String userType) {
		BigDecimal isaLimit = userType.equalsIgnoreCase("preferential")
			? new BigDecimal("4000000")
			: new BigDecimal("2000000");

		BigDecimal generalTaxRate = new BigDecimal("0.154"); // 일반 계좌 세율: 15.4%
		BigDecimal isaOverLimitTaxRate = new BigDecimal("0.099"); // ISA 초과분 분리과세: 9.9%

		// 일반 계좌 기준 세금 (isaProfit * generalTaxRate)
		BigDecimal taxIfGeneralAccount = totalProfit.multiply(generalTaxRate);

		BigDecimal taxFreeAmount; // ISA 한도 내 금액
		BigDecimal taxableExcessAmount = BigDecimal.ZERO;
		BigDecimal actualIsaTax = BigDecimal.ZERO;

		if (totalProfit.compareTo(isaLimit) <= 0) {
			taxFreeAmount = totalProfit;
		} else {
			taxFreeAmount = isaLimit;
			taxableExcessAmount = totalProfit.subtract(isaLimit);
			actualIsaTax = taxableExcessAmount.multiply(isaOverLimitTaxRate);
		}

		// 절세 금액 = 일반 계좌 세금 - ISA 세금
		BigDecimal totalTaxSaved = taxIfGeneralAccount.subtract(actualIsaTax);

		IsaTaxSavingHistoryVo vo = new IsaTaxSavingHistoryVo();
		vo.setMemberId(memberId);
		vo.setQuarter(quarter);
		vo.setIsaProfit(totalProfit.setScale(0, RoundingMode.DOWN).longValue()); // 소수점 버림 후 Long 변환
		vo.setGeneralTax(taxIfGeneralAccount.setScale(0, RoundingMode.DOWN).longValue());
		vo.setTaxSaved(totalTaxSaved.setScale(0, RoundingMode.DOWN).longValue());

		return vo;
	}
}
