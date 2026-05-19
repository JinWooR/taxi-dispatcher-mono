-- Driver Service Schema
CREATE SCHEMA IF NOT EXISTS driver_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE driver_db;

CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    driver_id VARCHAR(36) UNIQUE NOT NULL COMMENT '기사 ID (UUID)',
    account_id VARCHAR(36) UNIQUE NOT NULL COMMENT 'Account Service 계정 ID',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    phone_number VARCHAR(20) COMMENT '전화번호',
    license_number VARCHAR(50) COMMENT '면허번호',
    plate_number VARCHAR(20) COMMENT '차량번호',
    vehicle_type VARCHAR(50) COMMENT '차종',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE' COMMENT '상태 (OFFLINE, ONLINE, BUSY)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    UNIQUE KEY uk_account_id (account_id),
    UNIQUE KEY uk_driver_id (driver_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='택시 기사 정보';
