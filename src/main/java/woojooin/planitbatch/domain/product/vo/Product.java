package woojooin.planitbatch.domain.product.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import woojooin.planitbatch.domain.product.enums.InvestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	private String shortenCode;               // 단축코드
	private InvestType investType;          // 투자 성향
	private LocalDate baseDate;             // 기준일자 (YYYYMMDD)
	private String isinCode;                // ISIN 코드
	private String itemName;                // 종목명
	private Integer closingPrice;           // 종가
	private Integer difference;             // 대비 (전일 대비 등락)
	private BigDecimal fluctuationRate;        // 등락률
	private BigDecimal netAssetValue;       // 순자산가치 (NAV)
	private Integer marketOpenPrice;        // 시가
	private Integer highPrice;              // 고가
	private Integer lowPrice;               // 저가
	private Long tradeQuantity;         // 거래량
	private Long tradePrice;         // 거래대금
	private Long marketTotalAmount;         // 시가총액
	private Long stockListingCount;              // 상장주식수
	private String baseIndexName;           // 기초지수명
	private BigDecimal baseIndexClosingPrice; // 기초지수 종가
	private Long netAssetTotalAmount;       // 순자산총액
}
