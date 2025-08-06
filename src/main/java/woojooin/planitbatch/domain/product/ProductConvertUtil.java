package woojooin.planitbatch.domain.product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import woojooin.planitbatch.domain.product.vo.Product;
import woojooin.planitbatch.global.util.openData.dto.price.etf.ETFPriceRes;

public class ProductConvertUtil {
	public static List<Product> openDataResToProductVoList(ETFPriceRes res) {
		List<Product> productVOList = new ArrayList<>();

		for (ETFPriceRes.Item item : res.getItems().getItem()) {
			productVOList.add(openDataToProductVO(item));
		}

		return productVOList;
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
}
