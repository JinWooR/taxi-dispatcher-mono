# 🚕 Taxi Dispatcher

택시 배차 MSA 시스템 (모노리스 → 마이크로서비스 전환 프로젝트)

---

## 📌 프로젝트 배경

**기존 구조** (모노리스)
- 저장소: [taxi-dispatcher](https://github.com/JinWooR/taxi-dispatcher)
- 단일 애플리케이션 내 도메인 격리
- 도메인 간 통신: 이벤트 리스너 또는 내부 API 호출

**현재 진행** (MSA 전환)
- 모노리스의 각 도메인을 **독립적인 서비스**로 분리
- 서비스 간 **비동기 메시징** (향후 Kafka 도입 예정)
- 필요시 **동기 API 호출**로 보완
- 공유 라이브러리 (common-lib, domain-models)로 기본 기능 통합

---

## ⚙️ 환경 설정

### 1. 환경 변수 파일 생성

`envs/*.env.example`을 복사하여 실제 값을 입력하세요.

**형식**
```bash
cp envs/<파일명>.env.example envs/<파일명>.env
```

**예시**
```bash
cp envs/common.env.example envs/common.env
cp envs/account.env.example envs/account.env
```

> 나머지 서비스(user, driver, dispatcher)도 동일한 방식으로 복사.

### 2. 환경 변수 구조

| 파일 | 역할 |
|------|------|
| `envs/common.env` | 모든 서비스 공유 (DB, JWT 공개키, Internal API Key) |
| `envs/account.env` | account-service 전용 (JWT 비밀키, 다른 서비스 호출 URL) |
| `envs/user.env`, `driver.env`, `dispatcher.env` | 각 서비스 DB URL |

> env 파일 값은 **로컬 실행 기준**입니다. 도커 환경에서는 `docker-compose.yml`의 `environment`가 일부 값(컨테이너명 기반 URL 등)을 덮어씁니다.

### 3. 로컬 Spring 실행 시 환경 적용

> 도커 환경(`docker-compose up`)에서는 별도 설정 불필요. 아래는 IDE에서 직접 Spring을 실행할 때 적용 방법입니다.

#### ✅ 옵션 A: VM Options (권장)

Spring Boot의 `spring.config.import` 메커니즘 사용. IDE 독립적이며 표준 방식.

**형식**
```
-Dspring.config.import=optional:file:./envs/common.env[.properties],optional:file:./envs/<서비스>.env[.properties]
```

**예시: account-service**
```
-Dspring.config.import=optional:file:./envs/common.env[.properties],optional:file:./envs/account.env[.properties]
```

> 나머지 서비스는 `<서비스>` 부분만 해당 서비스명으로 변경.
>
> `[.properties]`는 Spring Boot에 `.env` 파일을 properties 형식으로 파싱하라는 힌트입니다. **고정값이며 그대로 사용**하세요.

#### 옵션 B: IntelliJ Run Configuration (IntelliJ 한정)

Run Configuration → **Environment Variables** 옆 폴더 아이콘 → **Use environment variables from file** → 파일 경로 지정:

```
envs/common.env;envs/account.env
```

복수 파일은 세미콜론(`;`)으로 구분.

---

## 🚀 빠른 시작

### 사전 준비
- Docker & Docker Compose 설치
- 환경 변수 파일 생성 (위 [환경 설정](#️-환경-설정) 참고)

### 로컬 개발 환경 실행
```bash
docker-compose up -d
```

서비스 시작:
- Account Service: http://localhost:8081
- User Service: http://localhost:8082
- Driver Service: http://localhost:8083
- Dispatcher Service: http://localhost:8084

### 중지
```bash
docker-compose down
```

---

## 🌐 API 문서 (Swagger)

각 서비스별 API 문서:
- http://localhost:8081/swagger-ui.html (Account)
- http://localhost:8082/swagger-ui.html (User)
- http://localhost:8083/swagger-ui.html (Driver)
- http://localhost:8084/swagger-ui.html (Dispatcher)

---

## 📚 문서

- [기술 스택](docs/01-technology-stack.md)
- [프로젝트 구조](docs/02-monorepo-structure.md)
- [Git 컨벤션](docs/03-git-convention.md)
- [빌드 & 실행](docs/04-build-run.md)
- [API 규칙](docs/05-api-common-rules.md)
- [문제 해결](docs/troubleshooting/)
- [기술 결정](docs/decisions/)

---

**마지막 업데이트**: 2026-05-19
