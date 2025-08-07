package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;
import woojooin.planitbatch.global.util.IsaTaxCalculator;

@Component
@RequiredArgsConstructor
@Slf4j
public class IsaTaxSavingProcessor implements ItemProcessor<UserProductQuarterData, IsaTaxSavingHistoryVo> {
	@Override
	public IsaTaxSavingHistoryVo process(UserProductQuarterData item) throws Exception {
		Long memberId = item.getMemberId();
		String quarter = item.getQuarter();
		// "general" 타입 고정, 필요시 변경 가능
		String userType = "general";

		if (item.getTotalProfit() == null) {
			item.setTotalProfit(BigDecimal.ZERO);
		}

		return IsaTaxCalculator.calculateIsaTaxSavingHistoryVo(memberId, quarter, item.getTotalProfit(), userType);
	}
}
