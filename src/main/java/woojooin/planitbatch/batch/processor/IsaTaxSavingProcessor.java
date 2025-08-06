package woojooin.planitbatch.batch.processor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.mapper.IsaTaxSavingMapper;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;
import woojooin.planitbatch.global.util.DateUtils;
import woojooin.planitbatch.global.util.IsaTaxCalculator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IsaTaxSavingProcessor implements ItemProcessor<UserProductQuarterData, IsaTaxSavingHistoryVo> {

	private final IsaTaxSavingMapper mapper;

	@Override
	public IsaTaxSavingHistoryVo process(UserProductQuarterData item) throws Exception {
		BigDecimal totalProfit = item.getTotalProfit();
		Long memberId = item.getMemberId();

		IsaTaxCalculator.IsaTaxResult result = IsaTaxCalculator.calculateTaxSaving(totalProfit, "general");

		BigDecimal lastAccumulated = mapper.selectLatestAccumulatedSaving(memberId);
		if (lastAccumulated == null) lastAccumulated = BigDecimal.ZERO;

		BigDecimal currentSaving = result.getTotalTaxSaved();
		BigDecimal newAccumulated = lastAccumulated.add(currentSaving);

		IsaTaxSavingHistoryVo history = new IsaTaxSavingHistoryVo();
		history.setMemberId(memberId);
		history.setQuarter(item.getQuarter());
		history.setSavingAmount(currentSaving);
		history.setAccumulatedSaving(newAccumulated);
		history.setCreatedAt(LocalDateTime.now());

		return history;
	}
}
