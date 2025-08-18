package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.DepositVO;

@Mapper
public interface DepositMapper {

	/**
	 * 계좌번호로 예적금 계좌 정보를 조회합니다.
	 */
	DepositVO findByAccountNumber(@Param("accountNumber") String accountNumber);

	/**
	 * 새로운 예적금 계좌 정보를 삽입합니다.
	 */
	void insertDeposit(DepositVO account);

	/**
	 * 기존 예적금 계좌 정보를 업데이트합니다.
	 */
	void updateDeposit(DepositVO account);

	/**
	 * 모든 예적금 계좌 정보를 조회합니다.
	 */
	List<DepositVO> findAllDeposits();

	/**
	 * 회원 ID로 예적금 계좌 정보를 조회합니다.
	 */
	List<DepositVO> findByMemberId(@Param("memberId") Long memberId);

	/**
	 * UPSERT: INSERT 또는 UPDATE를 한 번에 처리 (성능 최적화)
	 */
	void upsertDeposit(DepositVO account);

	/**
	 * 배치 UPSERT: 여러 개의 계좌를 한 번의 쿼리로 처리 (최고 성능)
	 */
	void batchUpsertDeposit(List<DepositVO> accounts);
} 