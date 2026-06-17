-- ============================================================================
-- Movement History Service 데이터베이스 스키마
-- ============================================================================

-- 데이터베이스 생성
CREATE SCHEMA IF NOT EXISTS movement_history_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE movement_history_db;

-- ============================================================================
-- 1. Movement Segments 테이블
-- ============================================================================
-- 기사 근무 세션(work_session) 단위 이동 경로 segment 보관.
-- 배차 운행 중 segment 인 경우 dispatch_id 가 함께 채워짐.
-- 좌표 시퀀스는 Google Encoded Polyline (precision 5) 으로 저장.
CREATE TABLE IF NOT EXISTS movement_segments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Segment PK (auto increment)',
    work_session_id VARCHAR(36) NOT NULL COMMENT '근무 세션 ID (driver-service FK)',
    driver_id VARCHAR(36) NOT NULL COMMENT '기사 ID (driver-service FK)',
    dispatch_id VARCHAR(36) COMMENT '배차 ID (dispatcher-service FK, 배차 운행 중 segment 만)',
    segment_no INT NOT NULL COMMENT 'Segment 번호 (1부터, 동일 work_session 내 유일)',
    polyline TEXT NOT NULL COMMENT 'Google Encoded Polyline (precision 5)',
    status VARCHAR(20) NOT NULL COMMENT '상태 (IN_PROGRESS, COMPLETED)',
    started_at DATETIME(6) NOT NULL COMMENT 'Segment 시작 시각 (= 생성 시각, UTC)',
    ended_at DATETIME(6) COMMENT 'Segment 종료 시각 (IN_PROGRESS 시 null, UTC)',
    updated_at DATETIME(6) NOT NULL COMMENT 'polyline 갱신 / finalize 시각 (UTC)',

    -- 동일 work_session 내 segment_no 중복 방지 + work_session 단위 조회 인덱스 겸용
    UNIQUE KEY uk_session_segment (work_session_id, segment_no),

    -- 인덱스 정의
    INDEX idx_driver_started (driver_id, started_at),
    INDEX idx_dispatch_id (dispatch_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='기사 이동 segment 이력';

-- ============================================================================
-- 데이터베이스 초기화 완료
-- ============================================================================
-- 스키마 생성 완료: movement_history_db
-- 테이블:
--   - movement_segments: 기사 이동 segment (work_session 단위, 배차 운행 중 segment 는 dispatch_id 포함)
