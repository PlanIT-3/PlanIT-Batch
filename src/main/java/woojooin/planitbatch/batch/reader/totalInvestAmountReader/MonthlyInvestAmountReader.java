package woojooin.planitbatch.batch.reader.totalInvestAmountReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.totalInvestAmount.WeeklyReportMapper;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@StepScope
@Slf4j
public class MonthlyInvestAmountReader implements ItemReader<List<WeeklyInvestSummaryVo>> {

    private final WeeklyReportMapper weeklyReportMapper;

    @Value("#{jobParameters['targetDate']}")
    private String targetDate;

    private static final int PAGE_SIZE = 1000; // 멤버 수
    private int currentPage = 0;
    private int currentIndex = 0;
    private List<List<WeeklyInvestSummaryVo>> currentBatch; // 멤버별로 그룹핑된 리스트들
    private boolean hasMoreData = true;

    // 지난달 날짜 범위
    private String lastMonthStartDate;
    private String lastMonthEndDate;

    public MonthlyInvestAmountReader(WeeklyReportMapper weeklyReportMapper) {
        this.weeklyReportMapper = weeklyReportMapper;
        log.info("MonthlyReportAmountReader 초기화 완료 - 지난달 주간 데이터 처리");
    }

    @Override
    public List<WeeklyInvestSummaryVo> read() throws Exception {
        if (lastMonthStartDate == null || lastMonthEndDate == null) {
            calculateLastMonthRange();
        }

        if (currentBatch == null || currentIndex >= currentBatch.size()) {
            if (!hasMoreData) {
                log.info("더 이상 읽을 데이터가 없습니다. 총 {}페이지 처리 완료 (지난달: {} ~ {})",
                        currentPage, lastMonthStartDate, lastMonthEndDate);
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
                log.info("배치 조회 완료 - 더 이상 데이터가 없습니다. (지난달: {} ~ {})", lastMonthStartDate, lastMonthEndDate);
                hasMoreData = false;
                return null;
            }
        }

        List<WeeklyInvestSummaryVo> memberLastMonthWeeklyData = currentBatch.get(currentIndex++);

        return memberLastMonthWeeklyData;
    }

    @Transactional(readOnly = true)
    public List<List<WeeklyInvestSummaryVo>> fetchNextBatch() throws Exception {
        List<List<WeeklyInvestSummaryVo>> groupedData = null;

        try {
            Map<String, Object> params = new HashMap<>();

            params.put("startDate", lastMonthStartDate);
            params.put("endDate", lastMonthEndDate);

            int offset = currentPage * PAGE_SIZE;
            params.put("_skiprows", offset);
            params.put("_pagesize", PAGE_SIZE);

            List<Long> memberIds = weeklyReportMapper.selectMemberIdsForMonth(params);

            if (memberIds == null || memberIds.isEmpty()) {
                return new ArrayList<>();
            }

            groupedData = new ArrayList<>();

            for (Long memberId : memberIds) {
                Map<String, Object> memberParams = new HashMap<>();
                memberParams.put("memberId", memberId);
                memberParams.put("startDate", lastMonthStartDate);
                memberParams.put("endDate", lastMonthEndDate);

                List<WeeklyInvestSummaryVo> memberMonthlyWeeklyData =
                        weeklyReportMapper.selectMemberWeeklyDataForMonth(memberParams);

                if (memberMonthlyWeeklyData != null && !memberMonthlyWeeklyData.isEmpty()) {
                    groupedData.add(memberMonthlyWeeklyData);
                    log.debug("멤버 {} 지난달 주간 데이터 {}건 조회", memberId, memberMonthlyWeeklyData.size());
                }
            }

            currentPage++;

            if (memberIds.size() < PAGE_SIZE) {
                hasMoreData = false;
            }

            return groupedData;

        } catch (Exception e) {
            log.error("지난달 주간 그룹 데이터 조회 실패 - 페이지: {}, 지난달범위: {} ~ {}, error: {}",
                    currentPage, lastMonthStartDate, lastMonthEndDate, e.getMessage(), e);
            throw e;

        } finally {
            log.debug("지난달 주간 그룹 조회 완료 - 페이지: {}, 결과 크기: {}",
                    currentPage - 1, groupedData != null ? groupedData.size() : 0);
        }
    }

    private void calculateLastMonthRange() {
        LocalDate baseDate;

        // targetDate가 있으면 해당 날짜 기준, 없으면 현재 날짜 기준
        if (targetDate != null && !targetDate.trim().isEmpty() && !targetDate.equals("null")) {
            try {
                baseDate = LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                log.info("사용자 지정 기준 날짜: {}", baseDate);
            } catch (Exception e) {
                log.warn("targetDate 파싱 실패, 현재 날짜 사용: {}", targetDate);
                baseDate = LocalDate.now();
            }
        } else {
            baseDate = LocalDate.now();
        }
        LocalDate lastMonthStart = baseDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastMonthEnd = baseDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        lastMonthStartDate = lastMonthStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lastMonthEndDate = lastMonthEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        log.info("지난달 범위 계산 완료 - 기준날짜: {}, 지난달범위: {} ~ {} ({}년 {}월)",
                baseDate, lastMonthStartDate, lastMonthEndDate,
                lastMonthStart.getYear(), lastMonthStart.getMonthValue());
    }
}
