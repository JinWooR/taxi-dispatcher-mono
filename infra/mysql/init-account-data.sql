-- ============================================================================
-- Account Service 테스트 데이터
-- ============================================================================

USE account_db;

-- ============================================================================
-- 1. 테스트 계정 데이터
-- ============================================================================

-- user001 계정
INSERT INTO accounts (account_id, status, created_at, updated_at)
VALUES (
    '4241d9d8-5649-4372-b758-0e5d57846ff0',
    'ACTIVE',
    NOW(),
    NOW()
);

-- driver001 계정
INSERT INTO accounts (account_id, status, created_at, updated_at)
VALUES (
    '0a640315-4366-47a4-b750-a37470540f07',
    'ACTIVE',
    NOW(),
    NOW()
);

-- ============================================================================
-- 2. 기본 인증(BASIC) 크레덴셜 데이터
-- ============================================================================

-- user001@naver.com (password: user001)
INSERT INTO credentials (
    credential_id,
    account_id,
    credential_type,
    login_id,
    hashed_password,
    registered_at,
    last_used_at
) VALUES (
    '665504ff-e9e3-4d62-b18b-c5e778afb66c',
    '4241d9d8-5649-4372-b758-0e5d57846ff0',
    'BASIC',
    'user001@naver.com',
    '$2a$10$PoLi6Z2RWcTkW6DTGYQrq.xxku94mlpaVvyS0rNPK7HFLRwa.HYiO',
    '2026-05-29 05:37:10',
    NULL
);

-- driver001@naver.com (password: driver001)
INSERT INTO credentials (
    credential_id,
    account_id,
    credential_type,
    login_id,
    hashed_password,
    registered_at,
    last_used_at
) VALUES (
    '6a92f9d8-b177-48c4-9dc9-7393aa450d7e',
    '0a640315-4366-47a4-b750-a37470540f07',
    'BASIC',
    'driver001@naver.com',
    '$2a$10$4P9uahveOX3uMQfyBdEkz.GXhYSxcCWDo0a.X501eR/gjp4U/oNG.',
    '2026-05-29 05:37:44',
    NULL
);

-- ============================================================================
-- 테스트 데이터 초기화 완료
-- ============================================================================
-- 생성된 데이터:
--   - 계정 2개 (ACTIVE 상태)
--   - user001@naver.com / user001
--   - driver001@naver.com / driver001
