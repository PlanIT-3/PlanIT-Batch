package woojooin.planitbatch.global.util.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.product.enums.InvestType;
import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;

@Slf4j
public class ProductCal {

	private static final Pattern LEVER_P = Pattern.compile(
		"(레버리지|\\b2x\\b|\\b3x\\b|lever|ultra|daily\\s*(2x|3x))",
		Pattern.CASE_INSENSITIVE
	);
	private static final Pattern INVERSE_P = Pattern.compile(
		"(인버스|\\b-1x\\b|\\b-2x\\b|inverse|bear)",
		Pattern.CASE_INSENSITIVE
	);
	private static final Pattern BOND_P = Pattern.compile(
		"(채권|회사채|국채|국공채|크레딧|bond|treasury|corporate\\s*bond|credit)",
		Pattern.CASE_INSENSITIVE
	);

	// 혼합/커버드콜/액티브는 참고용
	private static final Pattern MIX_HINT_P = Pattern.compile(
		"(커버드콜|밸런스|혼합|액티브)", Pattern.CASE_INSENSITIVE
	);

	// 1) 지표별 점수화 기준(임계값)
	private static final double[] VOL_THRESHOLDS = {0.10, 0.20, 0.30, 0.40, 0.50};
	private static final double[] VAR_THRESHOLDS = {0.02, 0.04, 0.06, 0.08, 0.10};
	private static final double[] MDD_THRESHOLDS = {0.10, 0.20, 0.30, 0.40, 0.50};
	private static final double[] INTRADAY_THRESHOLDS = {0.01, 0.02, 0.03, 0.04, 0.05};
	private static final double[] LIQ_THRESHOLDS = {0.20, 0.40, 0.60, 0.80, 1.00};

	// 2) 가중치
	private static final double W_VOL = 0.30;
	private static final double W_VAR = 0.25;
	private static final double W_MDD = 0.25;
	private static final double W_LIQUID = 0.20;

	/**
	 * ETF 가격 이력과 ETF 유형을 받아 1~6 등급을 반환합니다.
	 * history 리스트는 과거 순서(오래된 순)로 정렬되어 있어야 합니다.
	 */
	public static InvestType classify(List<EtfDailyHistory> history, String etfType, String shortenCode) {
		if (history == null || history.size() < 2) {
			log.error("ETF 상품 history 부족 shortenCode={}", shortenCode);
			return InvestType.VERY_AGGRESSIVE;
		}

		history.sort(Comparator.comparing(EtfDailyHistory::getBaseDate));

		// 1) 숫자 리스트 변환
		// 등락률(%) → 일일수익률(소수)로 변환: 0.930(%) → 0.00930
		List<Double> dailyReturns = history.stream()
			.map(it -> safe(it.getFluctuationRate())
				.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
				.doubleValue())
			.collect(Collectors.toList());

		List<Double> prices = history.stream()
			.map(it -> toDouble(it.getClosingPrice()))
			.collect(Collectors.toList());

		List<Double> intradayVols = history.stream()
			.map(it -> {
				double high = toDouble(it.getHighPrice());
				double low = toDouble(it.getLowPrice());
				double open = toDouble(it.getMarketOpenPrice());
				// 시가가 0(또는 null)이면 분모를 종가로 대체, 그마저도 0이면 0 반환
				if (open == 0.0) {
					open = toDouble(it.getClosingPrice());
					if (open == 0.0)
						return 0.0;
				}
				return (high - low) / open;
			})
			.collect(Collectors.toList());

		List<Double> volumes = history.stream()
			.map(it -> toDouble(it.getTradeQuantity()))
			.collect(Collectors.toList());

		// 2) 위험 지표 계산 (기존 유틸 재사용)
		double volAnnualized = calculateAnnualizedVolatility(dailyReturns);
		double var95 = calculateVaR(dailyReturns, 0.05);
		double mdd = calculateMaxDrawdown(prices);
		double intradayAvg = average(intradayVols);
		double liqRisk = coefficientOfVariation(volumes);

		// 3) 스코어링
		int scoreVol = score(volAnnualized, VOL_THRESHOLDS);
		int scoreVar = score(var95, VAR_THRESHOLDS);
		int scoreMDD = score(mdd, MDD_THRESHOLDS);
		int scoreIntra = score(intradayAvg, INTRADAY_THRESHOLDS);
		int scoreLiq = score(liqRisk, LIQ_THRESHOLDS);

		double scoreLiquidComposite = (scoreIntra + scoreLiq) / 2.0;

		// 4) 가중합
		double rawScore = scoreVol * W_VOL
			+ scoreVar * W_VAR
			+ scoreMDD * W_MDD
			+ scoreLiquidComposite * W_LIQUID;

		// 5) ETF 타입 보정
		double adj = 0;
		String type = etfType == null ? "" : etfType.toLowerCase();
		if (type.contains("lever") || type.contains("inverse")) {
			adj = +1.0;
		} else if (type.contains("bond")) {
			adj = -0.5;
		}

		// 6) 등급 산출 (1~6)
		int grade = clamp((int)Math.round(rawScore + adj), 1, 6);

		// 7) InvestType 매핑
		switch (grade) {
			case 1:
				return InvestType.SAFE;
			case 2:
				return InvestType.CONSERVATIVE;
			case 3:
				return InvestType.MODERATE;
			case 4:
				return InvestType.AGGRESSIVE;
			default:
				return InvestType.VERY_AGGRESSIVE;
		}
	}

	private static BigDecimal safe(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	private static double toDouble(Integer v) {
		return v == null ? 0.0 : v.doubleValue();
	}

	private static double toDouble(Long v) {
		return v == null ? 0.0 : v.doubleValue();
	}

	// ——————————————————————————————————————
	// 지표 계산 함수들

	private static double calculateAnnualizedVolatility(List<Double> returns) {
		double mean = average(returns);
		double variance = returns.stream()
			.mapToDouble(r -> Math.pow(r - mean, 2))
			.average().orElse(0.0);
		double stdDev = Math.sqrt(variance);
		return stdDev * Math.sqrt(252);
	}

	private static double calculateVaR(List<Double> returns, double p) {
		List<Double> sorted = new ArrayList<>(returns);
		Collections.sort(sorted);
		int idx = (int)Math.floor(sorted.size() * p);
		return -sorted.get(idx);
	}

	private static double calculateMaxDrawdown(List<Double> prices) {
		double peak = prices.get(0);
		double maxDD = 0.0;
		for (double price : prices) {
			peak = Math.max(peak, price);
			maxDD = Math.max(maxDD, (peak - price) / peak);
		}
		return maxDD;
	}

	private static double average(List<Double> list) {
		return list.stream().mapToDouble(d -> d).average().orElse(0.0);
	}

	private static double coefficientOfVariation(List<Double> list) {
		double mean = average(list);
		if (mean == 0)
			return 0.0;
		double variance = list.stream()
			.mapToDouble(v -> Math.pow(v - mean, 2))
			.average().orElse(0.0);
		return Math.sqrt(variance) / mean;
	}

	private static int score(double value, double[] thresholds) {
		for (int i = 0; i < thresholds.length; i++) {
			if (value <= thresholds[i]) {
				return i + 1;
			}
		}
		return thresholds.length + 1;
	}

	private static int clamp(int x, int min, int max) {
		return Math.max(min, Math.min(max, x));
	}

	/**
	 * itemName / baseIndexName에서 타입을 추론해
	 * classify()의 etfType에 넣을 문자열을 반환합니다.
	 * 반환값: "leveraged" | "inverse" | "bond" | ""(보정 없음)
	 */
	public static String inferEtfType(String itemName, String baseIndexName) {
		String src = ((itemName == null ? "" : itemName) + " " +
			(baseIndexName == null ? "" : baseIndexName)).toLowerCase();

		if (LEVER_P.matcher(src).find())
			return "leveraged";
		if (INVERSE_P.matcher(src).find())
			return "inverse";

		boolean hasBond = BOND_P.matcher(src).find();
		if (hasBond) {
			// 혼합/커버드콜 여부는 참고용이지만, 우리 보정은 -0.5라 과하지 않음 → bond 유지
			return "bond";
		}

		return "";
	}
}
