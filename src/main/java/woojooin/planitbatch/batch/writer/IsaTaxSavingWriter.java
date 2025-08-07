package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.IsaTaxSavingMapper;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

@Component
@RequiredArgsConstructor
@Slf4j
public class IsaTaxSavingWriter implements ItemWriter<IsaTaxSavingHistoryVo> {

	private final IsaTaxSavingMapper mapper;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void write(List<? extends IsaTaxSavingHistoryVo> items) throws Exception {
		log.info("writer!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		log.info("data!!!!!!!!!!!!!!!!!: {}", items);

		try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
			IsaTaxSavingMapper mapper = sqlSession.getMapper(IsaTaxSavingMapper.class);
			for (IsaTaxSavingHistoryVo item : items) {
				mapper.upsertIsaTaxSavingHistory(item);
			}
			sqlSession.commit(); // 꼭 커밋해야 함!
		} catch (Exception e) {
			System.err.println("[Insert/Update 배치 실패] 아이템 개수: " + items.size());
			e.printStackTrace();
		}
	}


}
