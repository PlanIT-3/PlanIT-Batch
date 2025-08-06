package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.mapper.IsaTaxSavingMapper;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

@Component
@RequiredArgsConstructor
public class IsaTaxSavingWriter implements ItemWriter<IsaTaxSavingHistoryVo> {

	private final IsaTaxSavingMapper mapper;

	@Override
	public void write(List<? extends IsaTaxSavingHistoryVo> items) throws Exception {
		for (IsaTaxSavingHistoryVo history : items) {
			mapper.insertTaxSavingHistory(history);
		}
	}
}