package woojooin.planitbatch.batch.reader.totalInvestAmountReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.MemberProductMapper;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MemberVo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@StepScope
@Slf4j
public class DailyInvestAmountReader implements ItemReader<MemberVo> {

    private final MemberProductMapper memberProductMapper;

    @Value("#{jobParameters['targetDate']}")
    private String targetDate;

    private static final int PAGE_SIZE = 1000;
    private int currentPage = 0;
    private int currentIndex = 0;
    private List<MemberVo> currentBatch;
    private boolean hasMoreData = true;

    public DailyInvestAmountReader(MemberProductMapper memberProductMapper) {
        this.memberProductMapper = memberProductMapper;
        log.info("MyBatisReader 초기화 완료");
    }

    @Override
    public MemberVo read() throws Exception {
        if (currentBatch == null || currentIndex >= currentBatch.size()) {
            if (!hasMoreData) {
                log.info("더 이상 읽을 데이터가 없습니다. 총 {}페이지 처리 완료", currentPage);
                return null;
            }

            // 이전 배치 메모리 해제
            if (currentBatch != null) {
                currentBatch.clear();
                currentBatch = null;
            }

            currentBatch = fetchNextBatch();
            currentIndex = 0;

            if (currentBatch == null || currentBatch.isEmpty()) {
                log.info("배치 조회 완료 - 더 이상 데이터가 없습니다.");
                hasMoreData = false;
                return null;
            }
        }

        // 현재 배치에서 다음 멤버 반환
        MemberVo member = currentBatch.get(currentIndex++);

        if (log.isDebugEnabled()) {
            log.debug("처리 중인 멤버: {}, 상품 개수: {} (페이지: {}, 인덱스: {})",
                    member.getMemberId(),
                    member.getProducts() != null ? member.getProducts().size() : 0,
                    currentPage, currentIndex - 1);
        }

        return member;
    }

    @Transactional(readOnly = true)
    public List<MemberVo> fetchNextBatch() throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();

            String dateParam = getFormattedTargetDate();
            params.put("targetDate", dateParam);


            int offset = currentPage * PAGE_SIZE;
            params.put("_skiprows", offset);
            params.put("_pagesize", PAGE_SIZE);

            List<MemberVo> members = memberProductMapper.selectMemberWithProducts(params);

            currentPage++;

            if (members.isEmpty() || members.size() < PAGE_SIZE) {
                hasMoreData = false;
            }


            return members;

        } catch (Exception e) {
            log.error("배치 데이터 조회 실패 - 페이지: {}, targetDate: {}, error: {}",
                    currentPage, targetDate, e.getMessage(), e);
            throw e;
        }
    }

    private String getFormattedTargetDate() {
        if (targetDate != null && !targetDate.trim().isEmpty()&& !targetDate.equals("null")) {
            try {
                LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return targetDate;
            } catch (Exception e) {
                log.warn("targetDate 파싱 실패, 기본값 사용: {}", targetDate);
                return getPreviousDay();
            }
        } else {
            log.info("targetDate가 null이거나 비어있음, 어제 날짜 사용");

            return getPreviousDay();
        }
    }

    private String getPreviousDay() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}