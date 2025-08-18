package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Product {

	private String productId;

	/** 상품코드 */
	private String productIdentifier;

	/** 단축코드 */
	private String shortCode;

	/** 국제증권식별번호 코드 */
	private String internationalSecuritiesIdentificationNumberCode;

	/** 상품명 */
	private String productName;

	/** 기준일자 */
	private LocalDate baseDate;

	/** 종가 */
	private BigDecimal closingPrice;

	/** 전일 대비 (가격 변화) */
	private BigDecimal priceChangeFromPreviousDay;

	/** 등락률 (%) */
	private BigDecimal fluctuationRate;

	/** 시가 (시작가) */
	private BigDecimal openingPrice;

	/** 고가 */
	private BigDecimal highPrice;

	/** 저가 */
	private BigDecimal lowPrice;

	/** 거래량 */
	private Long tradingVolume;

	/** 거래대금 */
	private Long transactionAmount;

	/** 상장주식수 */
	private Long numberOfListedShares;

	/** 시가총액 */
	private BigDecimal marketCapitalization;

	/** 투자유형 */
	private String investmentType;
}
