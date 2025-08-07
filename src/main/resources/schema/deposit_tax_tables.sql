-- 예적금 세금 처리 테이블 생성 스크립트
USE plan_it;

-- 1. 예적금 세금 계산 결과 테이블
CREATE TABLE IF NOT EXISTS deposit_tax_calculation (
    tax_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '세금 계산 ID',
    account_id BIGINT NOT NULL COMMENT '계좌 ID (account 테이블 참조)',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member 테이블 참조)',
    
    -- 수익 정보
    interest_income DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '이자 수익',
    tax_base_amount DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '과세 기준 금액',
    
    -- 세금 계산
    income_tax DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '소득세 (14%)',
    local_income_tax DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '지방소득세 (1.4%)',
    total_tax DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '총 세금 (15.4%)',
    net_income DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '세후 실수익',
    
    -- 계산 기간
    calculation_year INT NOT NULL COMMENT '계산 연도',
    calculation_month INT NOT NULL COMMENT '계산 월',
    calculation_date DATE NOT NULL COMMENT '계산 기준일',
    
    -- 메타 정보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    -- 인덱스
    INDEX idx_account_date (account_id, calculation_date),
    INDEX idx_member_date (member_id, calculation_date),
    INDEX idx_year_month (calculation_year, calculation_month),
    
    -- 외래키 제약조건
    FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='예적금 세금 계산 결과';

-- 테이블 생성 확인
SELECT 'deposit_tax_calculation 테이블 생성 완료' AS status;

-- 생성된 테이블 확인
SHOW TABLES LIKE 'deposit_%';