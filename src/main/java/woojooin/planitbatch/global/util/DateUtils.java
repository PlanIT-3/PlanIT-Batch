package woojooin.planitbatch.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class DateUtils {

	//분기 계산
	public static String getCurrentQuarter() {
		LocalDate now = LocalDate.now();
		int quarter = (now.getMonthValue() - 1) / 3 + 1;
		return now.getYear() + "-Q" + quarter;
	}

	public static String getQuarter(LocalDate date) {
		int quarter = (date.getMonthValue() - 1) / 3 + 1;
		return date.getYear() + "-Q" + quarter;
	}

}
