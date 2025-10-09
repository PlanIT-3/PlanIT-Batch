package woojooin.planitbatch.domain.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;
import woojooin.planitbatch.domain.product.vo.Product;
import woojooin.planitbatch.global.util.openData.dto.price.etf.ETFPriceRes;
import woojooin.planitbatch.global.util.openData.dto.price.etf.KRXETFRes;

public class ProductConvertUtil {
	public static List<Product> openDataResToProductVoList(ETFPriceRes res) {
		List<Product> productVOList = new ArrayList<>();

		for (ETFPriceRes.Item item : res.getItems().getItem()) {
			productVOList.add(openDataToProductVO(item));
		}

		return productVOList;
	}

	public static EtfDailyHistory openDataToEtfDailyHistory(ETFPriceRes.Item item) {
		return EtfDailyHistory.builder()
			.shortenCode(item.getSrtnCd())
			.baseDate(LocalDate.parse(item.getBasDt(), DateTimeFormatter.BASIC_ISO_DATE))
			.isinCode(item.getIsinCd())
			.itemName(item.getItmsNm())
			.closingPrice(item.getClpr())
			.difference(item.getVs())
			.fluctuationRate(item.getFltRt())
			.netAssetValue(item.getNav())
			.marketOpenPrice(item.getMkp())
			.highPrice(item.getHipr())
			.lowPrice(item.getLopr())
			.tradeQuantity(item.getTrqu())
			.tradePrice(item.getTrPrc())
			.marketTotalAmount(item.getMrktTotAmt())
			.stockListingCount(item.getStLstgCnt())
			.baseIndexName(item.getBssIdxIdxNm())
			.baseIndexClosingPrice(item.getBssIdxClpr())
			.netAssetTotalAmount(item.getNPptTotAmt())
			.build();
	}

	public static Product openDataToProductVO(ETFPriceRes.Item item) {
		return Product.builder()
			.shortenCode(item.getSrtnCd())
			.baseDate(LocalDate.parse(item.getBasDt(), DateTimeFormatter.BASIC_ISO_DATE))
			.isinCode(item.getIsinCd())
			.itemName(item.getItmsNm())
			.closingPrice(item.getClpr())
			.difference(item.getVs())
			.fluctuationRate(item.getFltRt())
			.netAssetValue(item.getNav())
			.marketOpenPrice(item.getMkp())
			.highPrice(item.getHipr())
			.lowPrice(item.getLopr())
			.tradeQuantity(item.getTrqu())
			.tradePrice(item.getTrPrc())
			.marketTotalAmount(item.getMrktTotAmt())
			.stockListingCount(item.getStLstgCnt())
			.baseIndexName(item.getBssIdxIdxNm())
			.baseIndexClosingPrice(item.getBssIdxClpr())
			.netAssetTotalAmount(item.getNPptTotAmt())
			.build();
	}

	public static List<Product> krxResToProductVoList(KRXETFRes res) {
		List<Product> productList = new ArrayList<>();

		for (KRXETFRes.KRXEtfPriceInfo info : res.getOutBlock1()) {
			productList.add(krxToProductVO(info));
		}

		return productList;
	}

	public static Product krxToProductVO(KRXETFRes.KRXEtfPriceInfo info) {
		return Product.builder()
			.shortenCode(info.getIsuCd()) // 종목코드 (단축코드)
			.baseDate(LocalDate.parse(info.getBasDd(), DateTimeFormatter.BASIC_ISO_DATE))
			.isinCode(info.getIsuCd()) // 동일 설정
			.itemName(info.getIsuNm())

			.closingPrice(parseIntSafe(info.getTddClsprc()))
			.difference(parseIntSafe(info.getCmpprevddPrc()))
			.fluctuationRate(parseBigDecimalSafe(info.getFlucRt()))
			.netAssetValue(parseBigDecimalSafe(info.getNav()))
			.marketOpenPrice(parseIntSafe(info.getTddOpnprc()))
			.highPrice(parseIntSafe(info.getTddHgprc()))
			.lowPrice(parseIntSafe(info.getTddLwprc()))
			.tradeQuantity(parseLongSafe(info.getAccTrdvol()))
			.tradePrice(parseLongSafe(info.getAccTrdval()))
			.marketTotalAmount(parseLongSafe(info.getMktcap()))
			.stockListingCount(parseLongSafe(info.getListShrs()))
			.baseIndexName(info.getIdxIndNm())
			.baseIndexClosingPrice(parseBigDecimalSafe(info.getObjStkprcIdx()))
			.netAssetTotalAmount(parseLongSafe(info.getInvstasstNetasstTotamt()))

			// 계산필드 (KRX 응답에 없음)
			.expectedReturnRate(null)
			.build();
	}

	private static Integer parseIntSafe(String value) {
		try {
			return (value == null || value.isEmpty()) ? null : Integer.parseInt(value.replaceAll(",", ""));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Long parseLongSafe(String value) {
		try {
			return (value == null || value.isEmpty()) ? null : Long.parseLong(value.replaceAll(",", ""));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static BigDecimal parseBigDecimalSafe(String value) {
		try {
			return (value == null || value.isEmpty()) ? null : new BigDecimal(value.replaceAll(",", ""));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
