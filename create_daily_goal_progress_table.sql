-- DailyGoalProgress 테이블 생성 SQL
CREATE TABLE daily_goal_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    object_id BIGINT NOT NULL,
    progress DOUBLE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_object_id (object_id),
    INDEX idx_created_at (created_at)
);

-- 테이블 설명
-- object_id: Goal 테이블의 object_id를 참조
-- progress: 목표 달성률 (0.0 ~ 1.0+ 범위)
-- created_at: 배치 작업 실행 시점