package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.IsaTaxSavingMapper;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

@Component
@RequiredArgsConstructor
@Slf4j
public class IsaTaxSavingWriter implements ItemWriter<IsaTaxSavingHistoryVo> {

	private final SqlSessionFactory sqlSessionFactory;

	@Override
	public void write(List<? extends IsaTaxSavingHistoryVo> items) throws Exception {

		try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			IsaTaxSavingMapper mapper = sqlSession.getMapper(IsaTaxSavingMapper.class);
			for (IsaTaxSavingHistoryVo item : items) {
				mapper.upsertIsaTaxSavingHistory(item);
			}
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
