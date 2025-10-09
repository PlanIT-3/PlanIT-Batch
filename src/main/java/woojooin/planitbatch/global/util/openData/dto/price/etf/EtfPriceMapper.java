package woojooin.planitbatch.global.util.openData.dto.price.etf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;

/**
 * KRX / 공공데이터포털 ETF 데이터를 공통 도메인 EtfPrice로 변환하는 Mapper
 */
public class EtfPriceMapper {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	/** KRXEtfPriceInfo → EtfDailyHistory 변환 */
	public static EtfDailyHistory toEtfDailyHistory(KRXETFRes.KRXEtfPriceInfo dto) {
		return EtfDailyHistory.builder()
			.baseDate(parseDate(dto.getBasDd()))
			.shortenCode(dto.getIsuCd())
			.itemName(dto.getIsuNm())
			.closingPrice(parseInt(dto.getTddClsprc()))
			.difference(parseInt(dto.getCmpprevddPrc()))
			.fluctuationRate(parseBigDecimal(dto.getFlucRt()))
			.netAssetValue(parseBigDecimal(dto.getNav()))
			.marketOpenPrice(parseInt(dto.getTddOpnprc()))
			.highPrice(parseInt(dto.getTddHgprc()))
			.lowPrice(parseInt(dto.getTddLwprc()))
			.tradeQuantity(parseLong(dto.getAccTrdvol()))
			.tradePrice(parseLong(dto.getAccTrdval()))
			.marketTotalAmount(parseLong(dto.getMktcap()))
			.netAssetTotalAmount(parseLong(dto.getInvstasstNetasstTotamt()))
			.stockListingCount(parseLong(dto.getListShrs()))
			.baseIndexName(dto.getIdxIndNm())
			.baseIndexClosingPrice(parseBigDecimal(dto.getObjStkprcIdx()))
			.build();
	}

	/** String → LocalDate 변환 */
	private static LocalDate parseDate(String val) {
		try {
			return (val == null || val.isBlank()) ? null : LocalDate.parse(val.trim(), DATE_FORMATTER);
		} catch (Exception e) {
			return null;
		}
	}

	/** String → Integer 변환 */
	private static Integer parseInt(String val) {
		try {
			if (val == null || val.isBlank())
				return null;
			String clean = val.replaceAll(",", "").trim();
			return clean.isEmpty() ? null : Integer.valueOf(clean);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** String → Long 변환 */
	private static Long parseLong(String val) {
		try {
			if (val == null || val.isBlank())
				return null;
			String clean = val.replaceAll(",", "").trim();
			return clean.isEmpty() ? null : Long.valueOf(clean);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** String → BigDecimal 변환 */
	private static BigDecimal parseBigDecimal(String val) {
		try {
			if (val == null || val.isBlank())
				return null;
			String clean = val.replaceAll(",", "").trim();
			return clean.isEmpty() ? null : new BigDecimal(clean);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
