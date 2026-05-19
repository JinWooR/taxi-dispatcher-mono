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

## 🚀 빠른 시작

### 사전 준비
- Docker & Docker Compose 설치

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
