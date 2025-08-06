package woojooin.planitbatch.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IsaTaxCalculator {

	/**
	 * ISA 계좌의 절세 효과를 계산한다.
	 *
	 * @param totalProfit ISA 계좌 전체 수익
	 * @param userType "general" (일반형) 또는 "preferential" (서민형/청년형)
	 * @return IsaTaxResult (절세 이익 + 누적 절세)
	 */
	public static IsaTaxResult calculateTaxSaving(BigDecimal totalProfit, String userType) {
		BigDecimal isaLimit = userType.equalsIgnoreCase("preferential")
			? new BigDecimal("4000000")
			: new BigDecimal("2000000");

		BigDecimal generalTaxRate = new BigDecimal("0.154"); // 일반 계좌 세율: 15.4%
		BigDecimal isaOverLimitTaxRate = new BigDecimal("0.099"); // ISA 초과분 분리과세: 9.9%

		// 일반 계좌 기준 세금
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

		// 누적 절세 금액 = 일반 계좌 세금 - ISA 세금
		BigDecimal totalTaxSaved = taxIfGeneralAccount.subtract(actualIsaTax);

		return new IsaTaxResult(
			taxFreeAmount.setScale(2, RoundingMode.DOWN),
			taxableExcessAmount.setScale(2, RoundingMode.DOWN),
			actualIsaTax.setScale(2, RoundingMode.DOWN),
			totalTaxSaved.setScale(2, RoundingMode.DOWN)
		);
	}

	public static class IsaTaxResult {
		private BigDecimal taxFreeAmount;           // 한도 내 비과세 금액
		private BigDecimal taxableExcessAmount;     // 한도 초과 금액
		private BigDecimal actualIsaTax;            // ISA에서 실제 낸 세금 (9.9%)
		private BigDecimal totalTaxSaved;           // 일반 계좌 대비 절세 금액

		public IsaTaxResult(BigDecimal taxFreeAmount, BigDecimal taxableExcessAmount,
			BigDecimal actualIsaTax, BigDecimal totalTaxSaved) {
			this.taxFreeAmount = taxFreeAmount;
			this.taxableExcessAmount = taxableExcessAmount;
			this.actualIsaTax = actualIsaTax;
			this.totalTaxSaved = totalTaxSaved;
		}

		public BigDecimal getTaxFreeAmount() {
			return taxFreeAmount;
		}

		public BigDecimal getTaxableExcessAmount() {
			return taxableExcessAmount;
		}

		public BigDecimal getActualIsaTax() {
			return actualIsaTax;
		}

		public BigDecimal getTotalTaxSaved() {
			return totalTaxSaved;
		}

		// ISA 세금을 뺀 실제 수익
		public BigDecimal getActualProfitAmount(){
			BigDecimal actualAmount = taxableExcessAmount.subtract(actualIsaTax);
			return taxFreeAmount.add(actualAmount);
		}

		@Override
		public String toString() {
			return "IsaTaxResult{" +
				"taxFreeAmount=" + taxFreeAmount +
				", taxableExcessAmount=" + taxableExcessAmount +
				", actualIsaTax=" + actualIsaTax +
				", totalTaxSaved=" + totalTaxSaved +
				'}';
		}
	}
}
