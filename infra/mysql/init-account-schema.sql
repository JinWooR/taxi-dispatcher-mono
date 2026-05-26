-- ============================================================================
-- Account Service 데이터베이스 스키마
-- ============================================================================

-- 데이터베이스 생성
CREATE SCHEMA IF NOT EXISTS account_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE account_db;

-- ============================================================================
-- 1. Accounts 테이블
-- ============================================================================
-- Account 애그리게이트 루트
-- 사용자 계정 정보 저장
CREATE TABLE accounts (
    account_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '계정 UUID',
    status VARCHAR(20) NOT NULL COMMENT '계정 상태 (ACTIVE, INACTIVE, SUSPENDED)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    -- 인덱스 정의
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='계정 정보';

-- ============================================================================
-- 2. Credentials 테이블 (SINGLE_TABLE 상속 전략)
-- ============================================================================
-- 기본 인증(BASIC) 또는 OAuth 인증(OAUTH) 정보 저장
-- discriminator 컬럼(credential_type)으로 타입 구분
CREATE TABLE credentials (
    credential_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '크레덴셜 UUID',
    account_id VARCHAR(36) NOT NULL COMMENT '계정 UUID (FK)',
    credential_type VARCHAR(20) NOT NULL COMMENT '크레덴셜 타입 (BASIC, OAUTH)',
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록 일시',
    last_used_at TIMESTAMP NULL COMMENT '마지막 사용 일시',

    -- BASIC 타입 컬럼
    login_id VARCHAR(255) NULL UNIQUE COMMENT 'BASIC: 로그인 ID (이메일 등)',
    hashed_password VARCHAR(255) NULL COMMENT 'BASIC: 해시된 비밀번호',

    -- OAUTH 타입 컬럼
    oauth_kind VARCHAR(20) NULL COMMENT 'OAUTH: OAuth 제공자 (GOOGLE, GITHUB 등)',
    iss VARCHAR(255) NULL COMMENT 'OAUTH: Token Issuer',
    sub VARCHAR(255) NULL COMMENT 'OAUTH: Subject (사용자 고유 ID)',
    email_link VARCHAR(255) NULL COMMENT 'OAUTH: 이메일 링크',

    -- 외래키
    CONSTRAINT fk_credentials_account_id
        FOREIGN KEY (account_id)
        REFERENCES accounts(account_id)
        ON DELETE CASCADE,

    -- 인덱스
    INDEX idx_account_id (account_id),
    INDEX idx_credential_type (credential_type),
    INDEX idx_login_id (login_id),
    INDEX idx_oauth_kind (oauth_kind),
    UNIQUE KEY uk_basic_credential (account_id, credential_type, login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='인증 정보 (기본/OAuth)';

-- ============================================================================
-- 3. Refresh Tokens 테이블
-- ============================================================================
CREATE TABLE refresh_tokens (
    token_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '토큰 UUID',
    account_id VARCHAR(36) NOT NULL COMMENT '계정 UUID (FK)',
    token_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 해시된 토큰 값',
    role VARCHAR(20) NOT NULL COMMENT '발급 시점 권한 (NONE/USER/DRIVER)',
    actor VARCHAR(36) COMMENT '발급 시점 도메인 ID (userId/driverId)',
    expires_at TIMESTAMP NOT NULL COMMENT '만료 일시',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',

    CONSTRAINT fk_refresh_tokens_account_id
        FOREIGN KEY (account_id)
        REFERENCES accounts(account_id)
        ON DELETE CASCADE,

    INDEX idx_account_id (account_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Refresh Token 정보';

-- ============================================================================
-- 데이터베이스 초기화 완료
-- ============================================================================
-- 스키마 생성 완료: account_db
-- 테이블:
--   - accounts: 사용자 계정 정보
--   - credentials: 인증 정보 (BASIC/OAUTH)
--   - refresh_tokens: Refresh Token 정보
