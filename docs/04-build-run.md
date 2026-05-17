# 4️⃣ 빌드 및 실행 방법

**상태**: 완료  
**마지막 업데이트**: 2026-05-16

---

## 📖 개요

택시 배차 MSA 시스템의 로컬 개발 환경 구성 및 실행 방법을 정의합니다.

---

## 🛠️ 필수 설치 항목

### 개발 환경 요구사항

| 항목 | 버전 | 설치 방법 |
|------|------|---------|
| **Docker** | 최신 | [Docker 공식 설치](https://docs.docker.com/get-docker/) |
| **Docker Compose** | 최신 | Docker Desktop에 포함 |
| **Java** | 17 LTS | [OpenJDK 설치](https://openjdk.org/projects/jdk/17/) |
| **Gradle** | 최신 | 프로젝트의 Gradle Wrapper 사용 |
| **IDE** | 선택 | IntelliJ IDEA, VS Code 등 |

---

## 🚀 로컬 실행 방법

### Phase 1: Docker Compose로 전체 시스템 실행 (현재)

#### 1. 저장소 클론

```bash
git clone <repository-url>
cd taxi-dispatcher-mono
```

#### 2. Docker Compose 시작

```bash
docker-compose up -d
```

**실행 내용:**
- MySQL 8.0 (port 3306)
- Account Service (port 8081)
- User Service (port 8082)
- Driver Service (port 8083)
- Dispatcher Service (port 8084)

#### 3. 서비스 상태 확인

```bash
docker-compose ps
```

#### 4. 로그 확인

```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f account-service
```

#### 5. 종료

```bash
docker-compose down
```

---

### Phase 2: Kubernetes 프로덕션 배포

**목표**: Docker 기반 개발 환경 → Kubernetes 프로덕션 배포

**시기**: 개발 환경 안정화 후

**구성 예정:**
```yaml
# infra/kubernetes/
├── namespace.yaml
├── services/
│   ├── account-service.yaml
│   ├── user-service.yaml
│   ├── driver-service.yaml
│   └── dispatcher-service.yaml
├── deployments/
├── configmaps/
├── secrets/
└── ingress.yaml
```

**실행:**
```bash
kubectl apply -f infra/kubernetes/
```

---

### Phase 3: Kafka 기반 이벤트 아키텍처 전환

**목표**: 서비스 간 직접 의존성 제거 → 개별 서비스 독립 실행 가능

**시기**: Kubernetes 배포 확인 후

```yaml
# Phase 3에서 추가될 docker-compose.yml 변경사항
services:
  kafka:
    image: confluentinc/cp-kafka:latest
    # Kafka 설정
  
  # 개별 서비스를 독립적으로 실행 가능하도록 변경
  account-service:
    depends_on:
      - mysql
      - kafka
```

**Phase 3 진행 시 추가 문서:**
- Kafka 설정 가이드
- 이벤트 기반 통신 구현 (동기 → 비동기)
- 개별 서비스 독립 실행 방법

---

### Phase 4: Observability 구성

**목표**: 모니터링, 로깅, 분산 추적 통합

**시기**: Kafka 전환 완료 후

**구성 예정:**
- **Prometheus**: 메트릭 수집
- **Grafana**: 대시보드 시각화
- **ELK / Loki**: 중앙집중식 로깅
- **Jaeger / Zipkin**: 분산 추적 (분산 시스템 디버깅)

---

## 🏗️ Docker Compose 설정

### docker-compose.yml 구조

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: taxi-mysql
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: taxi_dispatcher
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./infra/mysql/init-schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - ./infra/mysql/init-data.sql:/docker-entrypoint-initdb.d/02-data.sql
    networks:
      - taxi-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  account-service:
    build:
      context: .
      dockerfile: services/account-service/Dockerfile
    container_name: account-service
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/account_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - taxi-network

  user-service:
    build:
      context: .
      dockerfile: services/user-service/Dockerfile
    container_name: user-service
    ports:
      - "8082:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/user_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - taxi-network

  driver-service:
    build:
      context: .
      dockerfile: services/driver-service/Dockerfile
    container_name: driver-service
    ports:
      - "8083:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/driver_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - taxi-network

  dispatcher-service:
    build:
      context: .
      dockerfile: services/dispatcher-service/Dockerfile
    container_name: dispatcher-service
    ports:
      - "8084:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/dispatcher_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - taxi-network

volumes:
  mysql_data:

networks:
  taxi-network:
    driver: bridge
```

---

## 📊 데이터베이스 초기화

### SQL 스크립트 구조

```
infra/mysql/
├── init-schema.sql    # 스키마 및 테이블 생성
└── init-data.sql      # 초기 테스트 데이터
```

### init-schema.sql 예시

```sql
-- Account DB
CREATE SCHEMA IF NOT EXISTS account_db;
USE account_db;

CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User DB
CREATE SCHEMA IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Driver DB
CREATE SCHEMA IF NOT EXISTS driver_db;
USE driver_db;

CREATE TABLE drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    vehicle_number VARCHAR(20),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dispatcher DB
CREATE SCHEMA IF NOT EXISTS dispatcher_db;
USE dispatcher_db;

CREATE TABLE dispatches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔗 서비스 API 접근

### 각 서비스 엔드포인트

| 서비스 | Port | Swagger UI | API Base |
|--------|------|-----------|----------|
| Account Service | 8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/api |
| User Service | 8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/api |
| Driver Service | 8083 | http://localhost:8083/swagger-ui.html | http://localhost:8083/api |
| Dispatcher Service | 8084 | http://localhost:8084/swagger-ui.html | http://localhost:8084/api |

---

## 🐛 문제 해결

### MySQL 연결 오류

```bash
# MySQL 로그 확인
docker-compose logs mysql

# MySQL 컨테이너 재시작
docker-compose restart mysql
```

### 포트 충돌

포트 8081-8084가 이미 사용 중이면 docker-compose.yml에서 수정:

```yaml
ports:
  - "9081:8080"  # 변경: 8081 → 9081
```

### 컨테이너 강제 종료

```bash
docker-compose down -v  # 볼륨도 삭제
docker system prune     # 미사용 이미지/컨테이너 삭제
```

---

## ✅ 확인 사항

- [ ] Docker, Docker Compose 설치 확인
- [ ] docker-compose.yml 구성 확인
- [ ] MySQL 초기화 스크립트 위치 확인
- [ ] 각 서비스 Dockerfile 준비
- [ ] 전체 시스템 실행 테스트

---

## 📝 다음 단계

1. docker-compose.yml 작성
2. 각 서비스 Dockerfile 생성
3. infra/mysql/ 스크립트 작성
4. 로컬 실행 테스트

**Phase 2**: Kafka 도입 시 이 문서 업데이트 예정

---

**승인 날짜**: 2026-05-16
