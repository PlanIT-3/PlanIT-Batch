package woojooin.planitbatch.domain.mapper;

import java.util.List;
import woojooin.planitbatch.domain.vo.DepositAccount;
import woojooin.planitbatch.domain.vo.DepositTaxResult;

/**
 * 예적금 관련 매퍼 인터페이스
 */
public interface DepositMapper {
    
    /**
     * 세금 계산이 필요한 예적금 계좌 목록 조회
     * @return 예적금 계좌 리스트
     */
    List<DepositAccount> selectDepositAccountsForTaxCalculation();
    
    /**
     * 특정 사용자의 예적금 계좌 목록 조회
     * @param userId 사용자 ID
     * @return 예적금 계좌 리스트
     */
    List<DepositAccount> selectDepositAccountsByUserId(Long userId);
    
    /**
     * 예적금 세금 계산 결과 저장
     * @param result 계산 결과
     * @return 저장된 레코드 수
     */
    int insertDepositTaxResult(DepositTaxResult result);
    
    /**
     * 예적금 세금 계산 결과 일괄 저장
     * @param results 계산 결과 리스트
     * @return 저장된 레코드 수
     */
    int insertDepositTaxResults(List<DepositTaxResult> results);
    
    /**
     * 계좌의 마지막 계산일 업데이트
     * @param accountId 계좌 ID
     * @return 업데이트된 레코드 수
     */
    int updateLastCalculationDate(Long accountId);
}