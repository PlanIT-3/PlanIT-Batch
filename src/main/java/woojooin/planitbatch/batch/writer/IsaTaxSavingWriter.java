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
			try {
				mapper.insertTaxSavingHistory(history);
			} catch (Exception e) {
				// 개별 실패 로깅
				System.err.println("[Insert 실패] memberId: " + history.getMemberId() + ", quarter: " + history.getQuarter());
				e.printStackTrace();
				// 실패 아이템 별도 처리 가능 (예: 실패 리스트에 저장, 알림 등)
			}
		}
	}

	//MyBatis 다중 Insert
	// @Override
	// public void write(List<? extends IsaTaxSavingHistoryVo> items) throws Exception {
	// 	mapper.insertTaxSavingHistoryBatch((List<IsaTaxSavingHistoryVo>) items);
	// }
}