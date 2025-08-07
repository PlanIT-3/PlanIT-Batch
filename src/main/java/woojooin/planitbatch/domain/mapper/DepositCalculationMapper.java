package woojooin.planitbatch.domain.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.DepositCalculationVO;

@Mapper
public interface DepositCalculationMapper {

    /**
     * 계산 결과를 데이터베이스에 저장
     */
    void insertCalculation(DepositCalculationVO calculation);
    
    /**
     * 여러 계산 결과를 배치로 저장
     */
    void insertCalculations(@Param("calculations") List<DepositCalculationVO> calculations);
    
    /**
     * 특정 계좌의 계산 이력 조회
     */
    List<DepositCalculationVO> findByAccountId(@Param("accountId") Long accountId);
    
    /**
     * 특정 날짜의 모든 계산 결과 조회
     */
    List<DepositCalculationVO> findByDate(@Param("date") LocalDateTime date);
    
    /**
     * 최근 N개의 계산 결과 조회
     */
    List<DepositCalculationVO> findRecentCalculations(@Param("limit") int limit);
    
    /**
     * 특정 계좌의 최근 계산 결과 조회
     */
    DepositCalculationVO findLatestByAccountId(@Param("accountId") Long accountId);
}