package woojooin.planitbatch.global.util.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;

public class RebalanceCalc {
	// ——————————————
	// 1) 회귀 기반 “일별” 기울기 계산
	// ——————————————
	private static BigDecimal calculateRegressionSlopePerDay(List<EtfDailyHistory> history) {
		if (history == null || history.size() < 2) {
			return BigDecimal.ZERO;
		}
		List<EtfDailyHistory> sorted = new ArrayList<>(history);
		sorted.sort(Comparator.comparing(EtfDailyHistory::getBaseDate));
		LocalDate start = sorted.get(0).getBaseDate();
		int n = sorted.size();
		BigDecimal nBD = BigDecimal.valueOf(n);
		BigDecimal sumX = BigDecimal.ZERO;
		BigDecimal sumY = BigDecimal.ZERO;
		BigDecimal sumXY = BigDecimal.ZERO;
		BigDecimal sumX2 = BigDecimal.ZERO;

		for (EtfDailyHistory e : sorted) {
			BigDecimal x = BigDecimal.valueOf(
				ChronoUnit.DAYS.between(start, e.getBaseDate())
			);
			BigDecimal y = BigDecimal.valueOf(e.getClosingPrice());
			sumX = sumX.add(x);
			sumY = sumY.add(y);
			sumXY = sumXY.add(x.multiply(y));
			sumX2 = sumX2.add(x.multiply(x));
		}

		BigDecimal numerator = nBD.multiply(sumXY).subtract(sumX.multiply(sumY));
		BigDecimal denominator = nBD.multiply(sumX2).subtract(sumX.multiply(sumX));
		if (denominator.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		// 일별 기울기
		return numerator
			.divide(denominator, 10, RoundingMode.HALF_UP);
	}

	// ——————————————
	// 2) 기존의 “월별” 회귀 기울기 (30일 기준)
	// ——————————————
	public static BigDecimal calculateRegressionSlopePerMonth(List<EtfDailyHistory> history) {
		BigDecimal slopePerDay = calculateRegressionSlopePerDay(history);
		// 30일 ≒ 1개월
		return slopePerDay
			.multiply(BigDecimal.valueOf(30))
			.setScale(6, RoundingMode.HALF_UP);
	}

	/**
	 * 회귀 기반 months 개월 후 예상 수익을 Integer 로 반환
	 */
	public static Integer predictProfitByRegression(List<EtfDailyHistory> history, int months) {
		if (months <= 0) {
			return 0;
		}
		BigDecimal slopePerMonth = calculateRegressionSlopePerMonth(history);
		BigDecimal profit = slopePerMonth
			.multiply(BigDecimal.valueOf(months))
			// 소수점 반올림 후 정수화
			.setScale(0, RoundingMode.HALF_UP);
		return profit.intValue();
	}

	/**
	 * 특정 날짜(targetDate)까지의 예상 수익을 Integer 로 반환
	 */
	public static Integer predictProfitUntilDate(
		List<EtfDailyHistory> history,
		LocalDate targetDate
	) {
		if (history == null || history.isEmpty() || targetDate == null) {
			return 0;
		}
		// 일별 기울기
		BigDecimal slopePerDay = calculateRegressionSlopePerDay(history);

		// 마지막 관측일
		LocalDate lastDate = history.stream()
			.map(EtfDailyHistory::getBaseDate)
			.max(Comparator.naturalOrder())
			.orElse(targetDate);

		long daysToGo = ChronoUnit.DAYS.between(lastDate, targetDate);
		if (daysToGo <= 0) {
			return 0;
		}

		BigDecimal profit = slopePerDay
			.multiply(BigDecimal.valueOf(daysToGo))
			.setScale(0, RoundingMode.HALF_UP);
		return profit.intValue();
	}

	/**
	 * n개월 후 예상 수익률(%) 계산
	 *  = (예상 이익 / 마지막 종가) * 100
	 *  소수점 둘째 자리까지, 반올림
	 */
	public static BigDecimal predictReturnPercentageMonths(
		List<EtfDailyHistory> history,
		int months
	) {
		if (history == null || history.isEmpty() || months <= 0) {
			return BigDecimal.ZERO.setScale(2);
		}

		BigDecimal lastPrice = BigDecimal.valueOf(
			history.stream()
				.max(Comparator.comparing(EtfDailyHistory::getBaseDate))
				.map(EtfDailyHistory::getClosingPrice)
				.orElse(0)
		);
		if (lastPrice.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO.setScale(2);
		}

		BigDecimal profit = BigDecimal.valueOf(
			predictProfitByRegression(history, months)
		);

		return profit
			.divide(lastPrice, 6, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100))
			.setScale(2, RoundingMode.HALF_UP);
	}

}
