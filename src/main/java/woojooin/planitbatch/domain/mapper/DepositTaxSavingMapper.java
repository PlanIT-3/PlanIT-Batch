package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.DepositTaxSavingVO;

@Mapper
public interface DepositTaxSavingMapper {

    /**
     * 예적금 세금 절약 내역을 저장합니다.
     */
    void insertDepositTaxSaving(DepositTaxSavingVO depositTaxSaving);

    /**
     * 회원별 예적금 세금 절약 내역을 조회합니다.
     */
    List<DepositTaxSavingVO> findByMemberId(@Param("memberId") Long memberId);

    /**
     * 계좌별 예적금 세금 절약 내역을 조회합니다.
     */
    List<DepositTaxSavingVO> findByAccountId(@Param("accountId") Long accountId);

    /**
     * 특정 분기의 예적금 세금 절약 내역을 조회합니다.
     */
    List<DepositTaxSavingVO> findByQuarter(@Param("quarter") String quarter);

    /**
     * 회원과 분기로 예적금 세금 절약 내역을 조회합니다.
     */
    DepositTaxSavingVO findByMemberIdAndQuarter(@Param("memberId") Long memberId, @Param("quarter") String quarter);

    /**
     * 기존 데이터를 업데이트합니다.
     */
    void updateDepositTaxSaving(DepositTaxSavingVO depositTaxSaving);

    /**
     * 중복 데이터가 있는지 확인합니다.
     */
    int countByMemberIdAndAccountIdAndQuarter(@Param("memberId") Long memberId, 
                                             @Param("accountId") Long accountId, 
                                             @Param("quarter") String quarter);
}