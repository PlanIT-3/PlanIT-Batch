package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.MemberMapper;
import woojooin.planitbatch.domain.vo.InvestmentRatio;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class InvestmentRatioWriter implements ItemWriter<List<InvestmentRatio>> {

    private final MemberMapper memberMapper;

    @Override
    public void write(List<? extends List<InvestmentRatio>> items) throws Exception {
        log.debug("투자성향 비율 bulk 업데이트 시작 - 청크 수: {}", items.size());

        try {
            int totalUpdateCount = 0;

            for (List<InvestmentRatio> ratioList : items) {
                if (ratioList != null && !ratioList.isEmpty()) {
                    memberMapper.updateInvestmentRatiosBulk(ratioList);
                    totalUpdateCount += ratioList.size();
                    log.debug("청크 {} 건 투자성향 비율 bulk 업데이트 완료", ratioList.size());
                }
            }

            log.info("투자성향 비율 bulk 업데이트 완료 - 총 {} 건", totalUpdateCount);

        } catch (Exception e) {
            log.error("투자성향 비율 bulk 업데이트 중 오류 발생", e);
            throw new Exception("Writer bulk 처리 실패", e);
        }
    }
}