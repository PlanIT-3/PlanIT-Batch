package woojooin.planitbatch.global.util.openData.dto;

import lombok.Data;

@Data
public class OpenApiResponse<T> {

	private HeaderBody<T> response;

	@Data
	public static class HeaderBody<T> {
		private Header header;
		private T body;
	}

	@Data
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}
}
