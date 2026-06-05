-- ============================================================================
-- Dispatcher Service 데이터베이스 스키마
-- ============================================================================

-- 데이터베이스 생성
CREATE SCHEMA IF NOT EXISTS dispatcher_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE dispatcher_db;

-- ============================================================================
-- 1. Dispatches 테이블
-- ============================================================================
-- Dispatch 애그리게이트 루트
-- 배차 요청, 진행 상태, 위치 정보 저장
CREATE TABLE IF NOT EXISTS dispatches (
    dispatch_id VARCHAR(36) PRIMARY KEY COMMENT '배차 UUID',
    customer_id VARCHAR(36) NOT NULL COMMENT '요청 고객 ID (customer-service FK)',
    driver_id VARCHAR(36) COMMENT '담당 기사 ID (driver-service FK, nullable)',
    dispatch_status VARCHAR(20) NOT NULL COMMENT '배차 상태 (REQUESTED, CANCELLED, FAILED, ASSIGNED, IN_PROGRESS, ARRIVED, COMPLETED)',

    -- 출발지 정보 (Location VO @Embeddable)
    departure_latitude DOUBLE NOT NULL COMMENT '출발지 위도',
    departure_longitude DOUBLE NOT NULL COMMENT '출발지 경도',
    departure_address VARCHAR(255) NOT NULL COMMENT '출발지 주소',

    -- 도착지 정보 (Location VO @Embeddable)
    arrival_latitude DOUBLE NOT NULL COMMENT '도착지 위도',
    arrival_longitude DOUBLE NOT NULL COMMENT '도착지 경도',
    arrival_address VARCHAR(255) NOT NULL COMMENT '도착지 주소',

    -- 탐색 범위 (SearchScope)
    current_scope INT NOT NULL DEFAULT 1 COMMENT '현재 탐색 단계 (1, 2, 3)',
    scope_started_at DATETIME NOT NULL COMMENT '현재 탐색 범위 시작 시간',

    -- 타임스탬프
    requested_at DATETIME NOT NULL COMMENT '배차 요청 시간',
    failed_at DATETIME COMMENT '배차 실패 시간',
    approved_at DATETIME COMMENT '배차 승인 시간',
    departed_at DATETIME COMMENT '출발 시간',
    arrived_at DATETIME COMMENT '목적지 도착 시간',
    completed_at DATETIME COMMENT '배차 완료 시간',

    -- 인덱스 정의
    INDEX idx_customer_id (customer_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_status (dispatch_status),
    INDEX idx_requested_at (requested_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배차 정보';

-- ============================================================================
-- 2. Dispatch Candidates 테이블
-- ============================================================================
-- 배차 후보 기사
-- 출발지 주변 기사에게 배차 요청 알림 후 응답 상태 관리
CREATE TABLE IF NOT EXISTS dispatch_candidates (
    candidate_id VARCHAR(36) PRIMARY KEY COMMENT '후보 UUID',
    dispatch_id VARCHAR(36) NOT NULL COMMENT '배차 ID',
    driver_id VARCHAR(36) NOT NULL COMMENT '기사 ID (driver-service FK)',
    status VARCHAR(30) NOT NULL COMMENT '상태 (REQUESTED, ACCEPTED, REJECTED, ACCEPT_CANCELLED, CUSTOMER_CANCELLED, TIMEOUT)',
    created_at DATETIME NOT NULL COMMENT '등록 일시',
    updated_at DATETIME NOT NULL COMMENT '수정 일시',

    -- 중복 방지: 한 배차에 동일 기사 중복 등록 불가
    UNIQUE KEY uk_dispatch_driver (dispatch_id, driver_id),

    -- 인덱스 정의
    INDEX idx_dispatch_id (dispatch_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배차 후보 기사';

-- ============================================================================
-- 데이터베이스 초기화 완료
-- ============================================================================
-- 스키마 생성 완료: dispatcher_db
-- 테이블:
--   - dispatches: 배차 정보 (요청, 상태, 위치, 탐색 범위, 타임스탬프)
--   - dispatch_candidates: 배차 후보 기사 (상태별 응답 관리)
