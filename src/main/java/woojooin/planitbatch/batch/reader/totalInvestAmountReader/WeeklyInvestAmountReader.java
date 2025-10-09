package woojooin.planitbatch.batch.reader.totalInvestAmountReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.totalInvestAmount.DailyReportMapper;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;

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
public class WeeklyInvestAmountReader implements ItemReader<List<DailyInvestSummaryVo>> {

    private final DailyReportMapper dailyReportMapper;

    @Value("#{jobParameters['targetDate']}")
    private String targetDate;

    private static final int PAGE_SIZE = 1000;
    private int currentPage = 0;
    private int currentIndex = 0;
    private List<List<DailyInvestSummaryVo>> currentBatch;
    private boolean hasMoreData = true;
    private String lastWeekStartDate;
    private String lastWeekEndDate;

    public WeeklyInvestAmountReader(DailyReportMapper dailyReportMapper) {
        this.dailyReportMapper = dailyReportMapper;
        log.info("WeeklyInvestAmountReader 초기화 완료 - 지난주 데이터 처리");
    }

    @Override
    public List<DailyInvestSummaryVo> read() throws Exception {
        if (lastWeekStartDate == null || lastWeekEndDate == null) {
            calculateLastWeekRange();
        }

        if (currentBatch == null || currentIndex >= currentBatch.size()) {
            if (!hasMoreData) {
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
                log.info("배치 조회 완료 - 더 이상 데이터가 없습니다. (지난주: {} ~ {})", lastWeekStartDate, lastWeekEndDate);
                hasMoreData = false;
                return null;
            }
        }

        List<DailyInvestSummaryVo> memberLastWeekData = currentBatch.get(currentIndex++);

        return memberLastWeekData;
    }

    @Transactional(readOnly = true)
    public List<List<DailyInvestSummaryVo>> fetchNextBatch() throws Exception {
        List<List<DailyInvestSummaryVo>> groupedData = null;

        try {
            Map<String, Object> params = new HashMap<>();

            params.put("startDate", lastWeekStartDate);
            params.put("endDate", lastWeekEndDate);

            int offset = currentPage * PAGE_SIZE;
            params.put("_skiprows", offset);
            params.put("_pagesize", PAGE_SIZE);

            List<Long> memberIds = dailyReportMapper.selectMemberIdsForWeek(params);

            if (memberIds == null || memberIds.isEmpty()) {
                log.info("조회된 멤버 ID가 없습니다 - 페이지: {}", currentPage);
                return new ArrayList<>();
            }

            groupedData = new ArrayList<>();

            for (Long memberId : memberIds) {
                Map<String, Object> memberParams = new HashMap<>();
                memberParams.put("memberId", memberId);
                memberParams.put("startDate", lastWeekStartDate);
                memberParams.put("endDate", lastWeekEndDate);

                List<DailyInvestSummaryVo> memberWeeklyData =
                        dailyReportMapper.selectMemberDailyDataForWeek(memberParams);

                if (memberWeeklyData != null && !memberWeeklyData.isEmpty()) {
                    groupedData.add(memberWeeklyData);
                    log.debug("멤버 {} 지난주 데이터 {}건 조회", memberId, memberWeeklyData.size());
                }
            }

            currentPage++;

            if (memberIds.size() < PAGE_SIZE) {
                hasMoreData = false;
            }

            return groupedData;

        } catch (Exception e) {
            log.error("지난주 그룹 데이터 조회 실패 - 페이지: {}, 지난주범위: {} ~ {}, error: {}",
                    currentPage, lastWeekStartDate, lastWeekEndDate, e.getMessage(), e);
            throw e;

        } finally {
            log.debug("지난주 그룹 조회 완료 - 페이지: {}, 결과 크기: {}",
                    currentPage - 1, groupedData != null ? groupedData.size() : 0);
        }
    }

    private void calculateLastWeekRange() {
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

        LocalDate lastWeekStart = baseDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = lastWeekStart.plusDays(6); // 일요일

        lastWeekStartDate = lastWeekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lastWeekEndDate = lastWeekEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}