-- ============================================================================
-- Account Service 테스트 데이터
-- ============================================================================

USE account_db;

-- ============================================================================
-- 1. 테스트 계정 데이터
-- ============================================================================

-- 테스트 계정 1: 기본 로그인 계정
INSERT INTO accounts (account_id, status, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    'ACTIVE',
    NOW(),
    NOW()
);

-- 테스트 계정 2: OAuth 계정
INSERT INTO accounts (account_id, status, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    'ACTIVE',
    NOW(),
    NOW()
);

-- ============================================================================
-- 2. 기본 인증(BASIC) 크레덴셜 데이터
-- ============================================================================

-- 테스트 계정 1의 BASIC 로그인
-- loginId: test@example.com
-- password: password123 (BCrypt 해시)
INSERT INTO credentials (
    credential_id,
    account_id,
    credential_type,
    login_id,
    hashed_password,
    registered_at,
    last_used_at
) VALUES (
    '660e8400-e29b-41d4-a716-446655440000',
    '550e8400-e29b-41d4-a716-446655440000',
    'BASIC',
    'test@example.com',
    '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy6QCLG',
    NOW(),
    NULL
);

-- ============================================================================
-- 3. OAuth 크레덴셜 데이터
-- ============================================================================

-- 테스트 계정 2의 OAuth (Google)
INSERT INTO credentials (
    credential_id,
    account_id,
    credential_type,
    oauth_kind,
    iss,
    sub,
    email_link,
    registered_at,
    last_used_at
) VALUES (
    '660e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440001',
    'OAUTH',
    'GOOGLE',
    'https://accounts.google.com',
    '123456789012345678901',
    'test-oauth@gmail.com',
    NOW(),
    NULL
);

-- ============================================================================
-- 테스트 데이터 초기화 완료
-- ============================================================================
-- 생성된 데이터:
--   - 계정 2개 (ACTIVE 상태)
--   - BASIC 크레덴셜 1개 (test@example.com / password123)
--   - OAUTH 크레덴셜 1개 (Google)
--
-- 로그인 테스트:
--   POST /api/auth/login
--   {
--     "loginId": "test@example.com",
--     "password": "password123"
--   }
