package woojooin.planitbatch.global.util.openData.dto.price.etf;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * ETF 공통 도메인 모델
 * (KRX / 공공데이터포털 응답을 일원화하여 저장 또는 분석에 활용)
 */
@Getter
@Builder
@ToString
public class EtfPrice {

	/** 기준일자 (YYYYMMDD) */
	private String baseDate;

	/** 종목코드 */
	private String code;

	/** 종목명 */
	private String name;

	/** 종가 */
	private BigDecimal closePrice;

	/** 전일 대비 */
	private BigDecimal diff;

	/** 등락률 */
	private BigDecimal fluctuationRate;

	/** 순자산가치 (NAV) */
	private BigDecimal nav;

	/** 시가 */
	private BigDecimal openPrice;

	/** 고가 */
	private BigDecimal highPrice;

	/** 저가 */
	private BigDecimal lowPrice;

	/** 거래량 */
	private BigDecimal tradeVolume;

	/** 거래대금 */
	private BigDecimal tradeValue;

	/** 시가총액 */
	private BigDecimal marketCap;

	/** 순자산총액 */
	private BigDecimal totalNetAsset;

	/** 상장좌수 */
	private BigDecimal listedShares;

	/** 기초지수명 */
	private String baseIndexName;

	/** 기초지수 종가 */
	private BigDecimal baseIndexClose;

	/** 기초지수 등락률 */
	private BigDecimal baseIndexFluctuation;
}
