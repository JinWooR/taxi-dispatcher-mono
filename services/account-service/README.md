# Account Service

택시 배차 MSA 시스템의 **Account Service** (계정 관리 서비스)

## 📋 개요

- **역할**: 계정 관리, 인증, JWT 토큰 발급
- **포트**: 8081
- **기술 스택**: Spring Boot 3.5.14, MySQL, JPA, JWT

## 🏗️ 아키텍처

### DDD 계층 구조
```
Presentation (API)
    ↓
Application (Service, DTO)
    ↓
Domain (Account, Credential, Repository)
    ↓
Infrastructure (JPA, Database)
```

## 📦 도메인 모델

### Account (집합근)
- **accountId** (UUID)
- **status** (ACTIVE, LOCKED, SUSPENDED, DELETED)
- **credentials** (List<Credential>)

### Credential (인증 수단)
- **BasicCredential**: 이메일 + 비밀번호
- **OAuthCredential**: SNS 인증 (Google, Apple, Kakao, Naver 등)

## 🚀 API 엔드포인트

### 1. 회원가입: `POST /auth/register`

**요청:**
```json
{
  "loginId": "user@example.com",
  "password": "securePassword123",
  "passwordConfirm": "securePassword123"
}
```

**응답 (200 OK):**
```json
{
  "code": "SUCCESS",
  "message": "회원가입 완료",
  "data": {
    "accountId": "550e8400-e29b-41d4-a716-446655440000",
    "loginId": "user@example.com",
    "status": "ACTIVE",
    "createdAt": "2026-05-17T10:30:22Z"
  },
  "timestamp": "2026-05-17T10:30:22Z"
}
```

### 2. 로그인: `POST /auth/login`

**요청:**
```json
{
  "loginId": "user@example.com",
  "password": "securePassword123",
  "userType": "USER"
}
```

**응답 (200 OK):**
```json
{
  "code": "SUCCESS",
  "message": "로그인 성공",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "accountId": "550e8400-e29b-41d4-a716-446655440000",
    "loginId": "user@example.com"
  },
  "timestamp": "2026-05-17T10:30:22Z"
}
```

## 🔐 JWT 토큰 (shared/common-lib)

- **제공자**: `JwtTokenProvider`
- **검증**: `validateAndGetUser(token)` → `AuthUser`
- **형식**: `Bearer {token}`

**JWT Claims:**
- `sub`: accountId
- `type`: USER | DRIVER
- `email`: 사용자 이메일

## 📊 데이터베이스 스키마

### accounts 테이블
```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(36) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### credentials 테이블 (SINGLE_TABLE 상속)
```sql
CREATE TABLE credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    credential_id VARCHAR(36) UNIQUE NOT NULL,
    account_id VARCHAR(36) NOT NULL,
    credential_type VARCHAR(20) NOT NULL,
    
    -- BasicCredential 컬럼
    login_id VARCHAR(255) UNIQUE,
    hashed_password VARCHAR(255),
    
    -- OAuthCredential 컬럼
    oauth_kind VARCHAR(20),
    iss VARCHAR(255),
    sub VARCHAR(255),
    email_link VARCHAR(255),
    
    registered_at TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP,
    
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
```

## 🔧 빌드 및 실행

### 로컬 개발
```bash
# 개발 환경으로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Docker
```bash
# 이미지 빌드
docker build -t account-service .

# 컨테이너 실행
docker run -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/account_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  account-service
```

## 📖 API 문서

Swagger UI: `http://localhost:8081/swagger-ui.html`

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew test --tests="*IT"
```

## 📌 주의사항

1. **JWT Secret Key**: 운영 환경에서는 환경 변수로 설정
2. **비밀번호**: Spring Security PasswordEncoder로 암호화됨
3. **CORS**: 개발(*, 모든 출처), 운영(특정 도메인만)

## 🔄 향후 개선

- [ ] OAuth 인증 구현 (Google, Apple, Kakao, Naver)
- [ ] 이메일 인증 (가입 확인)
- [ ] 비밀번호 재설정
- [ ] 토큰 갱신 (Refresh Token)
- [ ] Rate Limiting
- [ ] 2FA (Two-Factor Authentication)

---

**마지막 업데이트**: 2026-05-17
