package woojooin.planitbatch.global.util.openData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.global.util.ConnectionUtil;
import woojooin.planitbatch.global.util.openData.dto.OpenApiResponse;
import woojooin.planitbatch.global.util.openData.dto.price.etf.ETFPriceRes;

@Slf4j
@Component
public class OpenDataUtil {

	@Value("${open.api.base-url}")
	private String OPEN_API_BASE_URL;

	@Value("${open.api.service-key}")
	private String OPEN_API_SERVICE_KEY;

	/**
	 * 공공데이터 open-api 금융위원회_증권상품시세정보 - ETF 시세
	 * @return 응답 문자열
	 */
	public OpenApiResponse<ETFPriceRes> getETFPriceInfo() {

		StringBuilder uriBuilder = new StringBuilder();

		uriBuilder.append(OPEN_API_BASE_URL).append("/getETFPriceInfo")
			.append("?").append("serviceKey=").append(OPEN_API_SERVICE_KEY)
			.append("&").append("resultType=").append("json");

		String response = ConnectionUtil.sendRequest(uriBuilder.toString());

		TypeReference<OpenApiResponse<ETFPriceRes>> type = new TypeReference<>() {
		};
		OpenApiResponse<ETFPriceRes> ETFResponse = ConnectionUtil.decodeJsonStringToDto(response, type,
			ConnectionUtil.CAMEL);

		return ETFResponse;
	}

}

