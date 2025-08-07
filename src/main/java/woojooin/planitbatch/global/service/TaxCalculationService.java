package woojooin.planitbatch.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositTaxSavingVO;
import woojooin.planitbatch.domain.mapper.DepositTaxSavingMapper;

@Slf4j
@Service
@Transactional  // 클래스 레벨에서 기본 트랜잭션 설정
public class TaxCalculationService {

	@Autowired
	private DepositTaxSavingMapper depositTaxSavingMapper;

	/**
	 * 개별 세금 절약 내역을 저장합니다.
	 * 기존 데이터가 있으면 업데이트, 없으면 새로 생성합니다.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveTaxCalculation(DepositTaxSavingVO taxSaving) {
		try {
			// 동일한 트랜잭션/커넥션에서 중복 체크 → 저장/업데이트
			int existCount = depositTaxSavingMapper.countByMemberIdAndAccountIdAndQuarter(
				taxSaving.getMemberId(), taxSaving.getAccountId(), taxSaving.getQuarter());
			
			if (existCount == 0) {
				// 새로운 데이터 삽입
				depositTaxSavingMapper.insertDepositTaxSaving(taxSaving);
				log.debug("세금 절약 내역 저장 완료: 계좌={}", taxSaving.getAccountId());
			} else {
				// 기존 데이터 업데이트
				depositTaxSavingMapper.updateDepositTaxSaving(taxSaving);
				log.debug("세금 절약 내역 업데이트 완료: 계좌={}", taxSaving.getAccountId());
			}
			
		} catch (Exception e) {
			log.error("세금 절약 내역 저장 중 오류 발생: 계좌={}", taxSaving.getAccountId(), e);
			// @Transactional로 인해 자동 롤백됨
			throw new RuntimeException("세금 절약 내역 저장 실패: " + taxSaving.getAccountId(), e);
		}
	}
	
	/**
	 * 배치 처리를 위한 대량 저장 메서드 - 배치 UPSERT 방식으로 최고 성능 최적화
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveTaxCalculationBatch(java.util.List<DepositTaxSavingVO> taxSavings) {
		try {
			log.info("세금 절약 내역 배치 저장 시작: {} 건", taxSavings.size());
			
			// ✅ 최종 개선: 배치 UPSERT - 1000개를 단일 쿼리로 처리
			// 기존: 1000개 × 1개 쿼리 = 1000개 쿼리
			// 개선: 1000개 = 1개 배치 쿼리
			depositTaxSavingMapper.batchUpsertDepositTaxSaving(new java.util.ArrayList<>(taxSavings));
			
			log.info("세금 절약 내역 배치 저장 완료: {} 건 (배치 UPSERT 방식)", taxSavings.size());
			
		} catch (Exception e) {
			log.error("세금 절약 내역 배치 저장 중 오류 발생", e);
			// 전체 배치가 롤백됨 - 부분 성공 방지
			throw new RuntimeException("세금 절약 내역 배치 저장 실패", e);
		}
	}
}