package woojooin.planitbatch.global.util.openData.dto.price.etf;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ETFPriceRes {

	private int numOfRows;
	private int pageNo;
	private int totalCount;
	private Items items;

	@Data
	public static class Items {
		private List<Item> item;
	}

	@Data
	public static class Item {

		/** 기준일자 (YYYYMMDD) */
		@JsonProperty("basDt")
		//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
		private String basDt;

		/** 단축코드 */
		@JsonProperty("srtnCd")
		private String srtnCd;

		/** ISIN코드 */
		@JsonProperty("isinCd")
		private String isinCd;

		/** 종목명 */
		@JsonProperty("itmsNm")
		private String itmsNm;

		/** 종가 */
		@JsonProperty("clpr")
		private Integer clpr;

		/** 대비 (전일 대비 등락) */
		@JsonProperty("vs")
		private Integer vs;

		/** 등락률 */
		@JsonProperty("fltRt")
		private BigDecimal fltRt;

		/** 순자산가치 (NAV) */
		@JsonProperty("nav")
		private BigDecimal nav;

		/** 시가 */
		@JsonProperty("mkp")
		private Integer mkp;

		/** 고가 */
		@JsonProperty("hipr")
		private Integer hipr;

		/** 저가 */
		@JsonProperty("lopr")
		private Integer lopr;

		/** 거래량 */
		@JsonProperty("trqu")
		private Long trqu;

		/** 거래대금 */
		@JsonProperty("trPrc")
		private Long trPrc;

		/** 시가총액 */
		@JsonProperty("mrktTotAmt")
		private Long mrktTotAmt;

		/** 상장주식수 */
		@JsonProperty("stLstgCnt")
		private Long stLstgCnt;

		/** 기초지수명 */
		@JsonProperty("bssIdxIdxNm")
		private String bssIdxIdxNm;

		/** 기초지수 종가 */
		@JsonProperty("bssIdxClpr")
		private BigDecimal bssIdxClpr;

		/** 순자산총액 */
		@JsonProperty("nPptTotAmt")
		private Long nPptTotAmt;
	}
}
