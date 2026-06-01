-- ============================================================================
-- Customer Service 데이터베이스 스키마
-- ============================================================================

-- 데이터베이스 생성
CREATE SCHEMA IF NOT EXISTS customer_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE customer_db;

-- ============================================================================
-- 1. Customers 테이블
-- ============================================================================
-- Customer 애그리게이트 루트
-- 고객 프로필 정보 저장
CREATE TABLE customers (
    customer_id VARCHAR(36) PRIMARY KEY COMMENT '고객 UUID',
    account_id VARCHAR(36) NOT NULL UNIQUE COMMENT '계정 UUID (account-service FK)',
    name VARCHAR(255) NOT NULL COMMENT '고객 이름',
    phone VARCHAR(20) NOT NULL COMMENT '전화번호',
    status VARCHAR(20) NOT NULL COMMENT '고객 상태 (ACTIVE, DELETED)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    -- 인덱스 정의
    INDEX idx_account_id (account_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고객 프로필 정보';

-- ============================================================================
-- 데이터베이스 초기화 완료
-- ============================================================================
-- 스키마 생성 완료: customer_db
-- 테이블:
--   - customers: 고객 프로필 정보 (이름, 전화번호, 상태)
