package woojooin.planitbatch.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.DepositVO;
import woojooin.planitbatch.domain.mapper.DepositMapper;

@Slf4j
@Service
@Transactional  // 클래스 레벨에서 기본 트랜잭션 설정
public class DepositService {

	@Autowired
	private DepositMapper depositMapper;

	/**
	 * 예적금 계좌 정보를 데이터베이스에 저장합니다.
	 * 기존 계좌가 있으면 업데이트, 없으면 새로 생성합니다.
	 * 
	 * @Transactional - 단일 커넥션으로 SELECT → INSERT/UPDATE 처리
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveDeposit(DepositVO account) {
		try {
			// 동일한 트랜잭션/커넥션에서 조회 → 저장/업데이트
			DepositVO existingAccount = depositMapper.findByAccountNumber(account.getAccountNumber());
			
			if (existingAccount != null) {
				// 기존 계좌 업데이트
				account.setAccountId(existingAccount.getAccountId());
				depositMapper.updateDeposit(account);
				log.debug("계좌 업데이트 완료: {}", account.getAccountNumber());
			} else {
				// 새 계좌 생성
				depositMapper.insertDeposit(account);
				log.debug("계좌 생성 완료: {}", account.getAccountNumber());
			}
			
		} catch (Exception e) {
			log.error("계좌 저장 중 오류 발생: {}", account.getAccountNumber(), e);
			// @Transactional로 인해 자동 롤백됨
			throw new RuntimeException("계좌 저장 실패: " + account.getAccountNumber(), e);
		}
	}

	/**
	 * 모든 예적금 계좌 정보를 조회합니다.
	 */
	@Transactional(readOnly = true)  // 읽기 전용 트랜잭션
	public java.util.List<DepositVO> getAllDeposits() {
		return depositMapper.findAllDeposits();
	}
	
	/**
	 * 배치 처리를 위한 대량 저장 메서드 - 더 효율적인 트랜잭션 처리
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveDepositBatch(java.util.List<DepositVO> deposits) {
		try {
			log.info("배치 저장 시작: {} 건", deposits.size());
			
			for (DepositVO account : deposits) {
				// 동일한 트랜잭션 내에서 모든 계좌 처리
				DepositVO existingAccount = depositMapper.findByAccountNumber(account.getAccountNumber());
				
				if (existingAccount != null) {
					account.setAccountId(existingAccount.getAccountId());
					depositMapper.updateDeposit(account);
				} else {
					depositMapper.insertDeposit(account);
				}
			}
			
			log.info("배치 저장 완료: {} 건", deposits.size());
			
		} catch (Exception e) {
			log.error("배치 저장 중 오류 발생", e);
			// 전체 배치가 롤백됨 - 부분 성공 방지
			throw new RuntimeException("배치 저장 실패", e);
		}
	}
} 