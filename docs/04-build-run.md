# 4️⃣ 빌드 및 실행 방법

**상태**: 개선 완료  
**마지막 업데이트**: 2026-05-17

---

## 📖 개요

택시 배차 MSA 시스템의 로컬 개발 환경 구성 및 Docker Compose를 통한 배포 및 실행 방법을 정의합니다.

---

## 🛠️ 필수 설치 항목

### 개발 환경 요구사항

| 항목 | 버전 | 설치 방법 |
|------|------|---------|
| **Docker** | 20.10+ | [Docker 공식 설치](https://docs.docker.com/get-docker/) |
| **Docker Base Image** | eclipse-temurin:17 | Java 17 공식 이미지 |
| **Docker Compose** | 2.0+ | Docker Desktop에 포함 |
| **Java** | 17 LTS | [OpenJDK 설치](https://openjdk.org/projects/jdk/17/) |
| **Gradle** | 8.14+ | 프로젝트의 Gradle Wrapper 사용 (./gradlew) |
| **IDE** | 선택 | IntelliJ IDEA, VS Code 등 |
| **MySQL CLI (선택)** | 8.0+ | 로컬 DB 접속 시만 필요 |

---

## 🚀 빠른 시작 (3단계)

### 1️⃣ 저장소 클론 및 진입

```bash
git clone <repository-url>
cd taxi-dispatcher
```

### 2️⃣ Docker Compose 시작

```bash
# 개발 환경 시작 (MySQL + Account Service)
docker-compose up -d

# 실행 상태 확인
docker-compose ps
```

### 3️⃣ 서비스 접근

| 서비스 | URL |
|--------|-----|
| **Account Service Swagger** | http://localhost:8081/swagger-ui.html |
| **MySQL** | localhost:3306 (root / password) |

---

## 🔧 환경 변수 설정

### .env 파일 생성 (선택)

프로젝트 루트에 `.env` 파일을 생성하여 환경별 설정을 관리합니다.

```bash
# .env (프로젝트 루트)
# MySQL 설정
MYSQL_ROOT_PASSWORD=password
MYSQL_ROOT_HOST=%

# Spring 데이터소스
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# 환경 프로필
SPRING_PROFILES_ACTIVE=dev
```

### 환경별 설정

#### 로컬 개발 환경 (.env)

```bash
# 개발 환경: 상세 로깅, Hot Reload 활성화
MYSQL_ROOT_PASSWORD=password
SPRING_PROFILES_ACTIVE=dev
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

#### 프로덕션 환경

```bash
# 프로덕션 환경: 최소 로깅, 보안 강화
MYSQL_ROOT_PASSWORD=<strong-password>
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
```

---

## 📋 Docker Compose 상세 설정

### docker-compose.yml 구조

```yaml
version: '3.8'

services:
  # MySQL 데이터베이스
  mysql:
    image: mysql:8.0
    container_name: taxi-dispatcher-mysql
    platform: linux/amd64
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-password}
      MYSQL_ROOT_HOST: '%'
      TZ: 'Asia/Seoul'
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      # 초기화 스크립트 (숫자 순서대로 실행)
      - ./infra/mysql/init-account-schema.sql:/docker-entrypoint-initdb.d/01-account-schema.sql
      - ./infra/mysql/init-account-data.sql:/docker-entrypoint-initdb.d/02-account-data.sql
    networks:
      - taxi-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10
      interval: 10s

  # Account Service
  account-service:
    build:
      context: .
      dockerfile: services/account-service/Dockerfile
    container_name: account-service
    ports:
      - "8081:8080"
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/account_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME:-root}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD:-password}
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - taxi-network
    restart: unless-stopped

volumes:
  mysql_data:
    driver: local

networks:
  taxi-network:
    driver: bridge
```

### 주요 설정 항목

| 항목 | 설명 |
|------|------|
| **mysql_data** | MySQL 데이터 영속 저장소 |
| **taxi-network** | 서비스 간 통신 네트워크 |
| **healthcheck** | MySQL 준비 완료 감지 |
| **depends_on** | 서비스 시작 순서 제어 |

---

## 📊 데이터베이스 초기화 구조

### SQL 스크립트 위치

```
infra/mysql/
├── init-account-schema.sql    # Account DB 스키마
├── init-account-data.sql      # Account 테스트 데이터
├── init-customer-schema.sql   # (향후) Customer DB
├── init-customer-data.sql     # (향후) Customer 테스트 데이터
└── ...
```

### 초기화 프로세스

```
Docker Container Start
    ↓
MySQL 시작
    ↓
01-account-schema.sql 실행 (스키마 생성)
    ↓
02-account-data.sql 실행 (샘플 데이터)
    ↓
healthcheck 통과
    ↓
Account Service 시작
```

### Account DB 스키마

#### accounts 테이블

```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### credentials 테이블 (SINGLE_TABLE 상속)

```sql
CREATE TABLE credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    credential_id VARCHAR(36) NOT NULL UNIQUE,
    account_id VARCHAR(36) NOT NULL,
    credential_type VARCHAR(20) NOT NULL,  -- BASIC, OAUTH
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP NULL,
    
    -- BASIC 타입 컬럼
    login_id VARCHAR(255) NULL UNIQUE,
    hashed_password VARCHAR(255) NULL,
    
    -- OAUTH 타입 컬럼
    oauth_kind VARCHAR(20) NULL,
    iss VARCHAR(255) NULL,
    sub VARCHAR(255) NULL,
    email_link VARCHAR(255) NULL
);
```

### 테스트 데이터

#### 기본 로그인 (BASIC)

```json
{
  "loginId": "test@example.com",
  "password": "password123"
}
```

#### OAuth (Google)

- issuer: `https://accounts.google.com`
- subject: `123456789012345678901`

---

## 🚀 실행 방법

### Phase 1: Docker Compose (현재 - 동기 REST API)

#### 시작

```bash
# 백그라운드 실행
docker-compose up -d

# 포그라운드 실행 (로그 실시간 확인)
docker-compose up
```

#### 상태 확인

```bash
# 컨테이너 상태
docker-compose ps

# MySQL 상태
docker-compose exec mysql mysqladmin ping -u root -ppassword

# 서비스 접근 가능 확인
curl http://localhost:8081/swagger-ui.html
```

#### 종료

```bash
# 컨테이너 중지
docker-compose down

# 데이터 포함 완전 초기화
docker-compose down -v
```

---

## 📝 로깅 및 모니터링

### 실시간 로그 확인

```bash
# 전체 서비스 로그 (실시간)
docker-compose logs -f

# 특정 서비스만 (Account Service)
docker-compose logs -f account-service

# MySQL 로그 확인
docker-compose logs -f mysql

# 마지막 N줄만 확인
docker-compose logs -f account-service --tail=50
```

### 로그 레벨 제어

#### 개발 환경 (application-dev.yml)

```yaml
logging:
  level:
    root: INFO
    com.taxidispatcher: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

#### 프로덕션 환경 (application-prod.yml)

```yaml
logging:
  level:
    root: WARN
    com.taxidispatcher: INFO
    org.springframework: WARN
```

### 컨테이너 리소스 모니터링

```bash
# 실시간 리소스 사용량
docker stats

# MySQL 상세 정보
docker inspect taxi-dispatcher-mysql

# Account Service 상세 정보
docker inspect account-service
```

---

## 🔗 서비스 API 접근

### Account Service (현재)

| 항목 | URL |
|------|-----|
| **Swagger UI** | http://localhost:8081/swagger-ui.html |
| **API Docs** | http://localhost:8081/v3/api-docs |
| **Health Check** | http://localhost:8081/actuator/health |

### 향후 서비스 추가 예정

| 서비스 | Port | Swagger |
|--------|------|---------|
| Customer Service | 8082 | http://localhost:8082/swagger-ui.html |
| Driver Service | 8083 | http://localhost:8083/swagger-ui.html |
| Dispatcher Service | 8084 | http://localhost:8084/swagger-ui.html |

---

## 🐛 문제 해결

### MySQL 연결 오류

**증상**: `Access denied for user 'root'`

```bash
# MySQL 로그 확인
docker-compose logs mysql

# MySQL 재시작
docker-compose restart mysql

# MySQL 접속 테스트
docker-compose exec mysql mysql -u root -ppassword -e "SELECT 1"
```

### Account Service 시작 실패

**증상**: Account Service가 자꾸 Restarting 상태

```bash
# 서비스 로그 확인
docker-compose logs account-service

# 데이터베이스 연결 확인
docker-compose exec mysql mysql -u root -ppassword -D account_db -e "SHOW TABLES"

# 강제 재빌드
docker-compose down
docker-compose build --no-cache account-service
docker-compose up -d
```

### 포트 충돌

**증상**: `Bind for 0.0.0.0:3306 failed: port is already allocated`

```bash
# 사용 중인 포트 확인
lsof -i :3306

# 기존 컨테이너 정리
docker-compose down -v
docker system prune -a

# 포트 변경 (docker-compose.yml)
ports:
  - "3307:3306"  # 호스트 포트 변경: 3306 → 3307
```

### 디스크 용량 부족

**증상**: `no space left on device`

```bash
# Docker 시스템 정리
docker system prune -a --volumes

# MySQL 데이터 삭제
docker volume rm taxi-dispatcher_mysql_data

# 재시작
docker-compose up -d
```

### Hot Reload 작동 안 함

**현재 상태**: Docker 기반이므로 코드 변경 시 재빌드 필요

```bash
# 변경 후 재빌드
docker-compose build account-service
docker-compose up -d account-service
```

---

## 💡 개발 팁

### 빠른 재시작

```bash
# Account Service만 재빌드 및 재시작
docker-compose up -d --build account-service
```

### Dockerfile 이미지 정보

```dockerfile
# 빌드 단계
FROM eclipse-temurin:17-jdk-slim as builder

# 런타임 단계
FROM eclipse-temurin:17-jdk-slim
```

**주의**: ~~openjdk:17-slim~~ (지원 중단) → **eclipse-temurin:17-jdk-slim** (현재)

### 로그 저장

```bash
# 로그를 파일로 저장
docker-compose logs account-service > account-service.log

# MySQL 로그 저장
docker-compose logs mysql > mysql.log
```

### 데이터베이스 초기화 (재시작)

```bash
# 기존 데이터 삭제 및 재초기화
docker-compose down -v
docker-compose up -d
```

### 특정 쿼리 실행

```bash
# MySQL 접속 및 쿼리 실행
docker-compose exec mysql mysql -u root -ppassword -D account_db << EOF
SELECT * FROM accounts;
SELECT * FROM credentials;
EOF
```

---

## ✅ 배포 체크리스트

- [ ] Docker, Docker Compose 설치 확인
- [ ] 저장소 클론 및 진입 (`cd taxi-dispatcher`)
- [ ] `.env` 파일 설정 (선택)
- [ ] `docker-compose up -d` 실행
- [ ] `docker-compose ps`에서 모두 UP 상태 확인
- [ ] Account Service healthcheck 통과 확인
- [ ] Swagger UI 접근 가능 확인 (`http://localhost:8081/swagger-ui.html`)
- [ ] MySQL 테스트 데이터 확인
- [ ] 로그에 오류 없음 확인

---

## 🔄 아키텍처 진화 로드맵

### Phase 1 (현재): 동기 REST API

**특징:**
- 단일 MySQL 인스턴스 (서비스별 스키마 분리)
- 서비스 간 직접 호출 (동기)
- Docker Compose로 로컬 개발

**구성:**
```
MySQL 8.0
├── account_db
├── customer_db (향후)
├── driver_db (향후)
└── dispatcher_db (향후)
```

---

### Phase 2: Kubernetes 프로덕션 배포

**목표**: Docker 기반 개발 환경 → Kubernetes 프로덕션 배포

**시기**: 개발 환경 안정화 후

**예상 구조:**
```
infra/kubernetes/
├── namespace.yaml
├── services/
├── deployments/
├── configmaps/
├── secrets/
└── ingress.yaml
```

**예정 문서:**
- Kubernetes 설정 가이드
- Helm Chart 구성
- 배포 및 운영 가이드

---

### Phase 3: Kafka 기반 이벤트 아키텍처

**목표**: 서비스 간 비동기 이벤트 기반 통신

**변경 사항:**
```yaml
services:
  kafka:
    image: confluentinc/cp-kafka:latest
  
  account-service:
    depends_on:
      - mysql
      - kafka  # Kafka 의존성 추가
```

**예정 문서:**
- Kafka 설정 가이드
- 이벤트 기반 통신 구현
- 동기→비동기 마이그레이션 전략

---

### Phase 4: Observability 구성

**목표**: 모니터링, 로깅, 분산 추적 통합

**구성 예정:**
- **Prometheus**: 메트릭 수집
- **Grafana**: 대시보드 시각화
- **Loki**: 중앙집중식 로깅
- **Jaeger**: 분산 추적

---

## 📚 참고 자료

- [Docker Compose 공식 문서](https://docs.docker.com/compose/)
- [MySQL 8.0 Docker 이미지](https://hub.docker.com/_/mysql)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [프로젝트 구조 가이드](./02-monorepo-structure.md)
- [Git 컨벤션](./03-git-convention.md)

---

## 🤝 문제 및 피드백

문제 발생 또는 개선사항:
1. GitHub Issue 생성
2. PR로 수정 제안
3. 팀에 직접 보고

---

**마지막 업데이트**: 2026-05-17  
**버전**: 2.1 (Eclipse Temurin 이미지 업데이트)
