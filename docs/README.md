# 📚 Documentation Guide

택시 배차 MSA 시스템의 모든 문서 가이드입니다.
각 항목을 클릭하여 상세 내용을 확인하세요.

---

## 📖 전체 문서 목록

### 1️⃣ **[기술 스택 정의](./01-technology-stack.md)**

**내용**: 프로젝트에서 사용하는 모든 기술의 버전과 선택 근거

- Java 17, Spring Boot 3.5.14, Gradle
- MySQL 8.0, JPA/Hibernate
- Docker, Springdoc OpenAPI
- 미결정: Database Migration Tool

**언제 보나**: 개발 환경 설정, 의존성 추가 시

---

### 2️⃣ **[모노레포 프로젝트 구조](./02-monorepo-structure.md)**

**내용**: 모노레포 형식의 전체 디렉토리 및 모듈 구조

**초기 4개 마이크로서비스:**
- Account Service (계정 관리)
- User Service (사용자 관리)
- Driver Service (기사 관리)
- Dispatcher Service (배차 관리)

**공유 라이브러리:**
- common-lib (공통 예외, 상수, API 응답)
- domain-models (공유 도메인 모델)

**데이터베이스 전략:**
- 단일 MySQL 인스턴스
- 서비스별 스키마 분리 (account_db, user_db, driver_db, dispatcher_db)

**언제 보나**: 프로젝트 초기화, 모듈 생성 시

---

### 3️⃣ **[Git 컨벤션 및 규칙](./03-git-convention.md)**

**내용**: 커밋, 브랜치, PR 관리 규칙

**커밋 메시지:**
- Conventional Commits 형식
- 예: `feat(account): 회원가입 API 구현`

**브랜치 전략:**
- Trunk-Based Development
- main + feature/<service>/<feature-name>
- 예: `feature/account/login`, `feature/dispatcher/matching`

**PR 프로세스:**
- PR 제목: Conventional Commits 형식
- 최소 리뷰어: 1명
- 자동 테스트: PR 생성 시 자동 실행

**언제 보나**: 코드 작성 전, PR 생성 시

---

### 4️⃣ **[빌드 및 실행 방법](./04-build-run.md)**

**내용**: 로컬 개발 환경 구성 및 시스템 실행

**현재 (Phase 1):**
- Docker Compose로 MySQL + 4개 서비스 함께 실행
- 강한 서비스 의존성으로 인한 함께 실행 필요
- 각 서비스 포트: 8081~8084

**추후 (Phase 2):**
- Kafka 도입으로 비동기 이벤트 기반 통신
- 느슨한 결합(Loose Coupling) 달성
- 개별 서비스 독립 실행 가능

**MySQL 초기화:**
- SQL 스크립트 기반 (infra/mysql/init-schema.sql, init-data.sql)

**언제 보나**: 로컬 개발 시작, 환경 설정 시

---

### 5️⃣ **[API 공통 규칙](./05-api-common-rules.md)**

**내용**: 모든 API가 따를 공통 규칙

**표준 응답 포맷:**
```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "data": {...},
  "timestamp": "2025-12-03T03:32:22Z"
}
```

**주요 내용:**
- 성공/에러 응답 포맷 (status 제거)
- 에러 코드 체계 (ACCOUNT_NOT_FOUND 형식)
- JWT 기반 인증/인가
- Spring Validation 기반 유효성 검사
- API 문서: Springdoc OpenAPI (인터페이스 기반)
- 타임스탬프: Unix 저장, ISO 8601Z 응답
- CORS: 개발(*, 모든 출처), 운영(특정 도메인)

**언제 보나**: API 구현, 클라이언트 개발 시

---

### 6️⃣ **[데이터베이스 스키마 설계](./06-database-schema.md)** ⏳ (선택, 작성 예정)

**예상 내용:**
- 각 서비스별 스키마 설계
- JPA 엔티티 매핑
- DB 마이그레이션 전략

---

## 🎯 빠른 시작 가이드

### 처음 온 개발자라면?

1. **[기술 스택](./01-technology-stack.md)** 읽기 → 개발 환경 설정
2. **[모노레포 구조](./02-monorepo-structure.md)** 읽기 → 프로젝트 이해
3. **[빌드 및 실행](./04-build-run.md)** 읽기 → 로컬 환경 구성
4. **[Git 컨벤션](./03-git-convention.md)** 읽기 → 개발 시작

---

## 📋 문서 상태

| 문서 | 상태 | 마지막 수정 |
|------|------|-----------|
| 01-technology-stack.md | ✅ 완료 | 2026-05-16 |
| 02-monorepo-structure.md | ✅ 완료 | 2026-05-16 |
| 03-git-convention.md | ✅ 완료 | 2026-05-16 |
| 04-build-run.md | ✅ 완료 | 2026-05-17 |
| 05-api-common-rules.md | ✅ 완료 | 2026-05-16 |
| 06-database-schema.md | ⏳ 선택 | - |

---

## 🔄 아키텍처 진화 로드맵

### Phase 1 (현재): 초기 개발 ✅

**상태**: 완료
**구성**: REST API (동기) + Docker Compose

```
동기 REST API
└─ 강한 서비스 의존성
   └─ Docker Compose 함께 실행
   └─ 개발 환경 구축 완료
```

**관련 문서**: 01~05번

---

### Phase 2: Kubernetes 프로덕션 배포 📦

**상태**: 예정
**구성**: Kubernetes + Docker

```
Docker 이미지 → Kubernetes 클러스터
└─ 프로덕션 환경 배포
   └─ 자동 스케일링, 헬스 체크
   └─ 실제 동작 확인
```

**예정 문서**:
- Kubernetes 설정 가이드
- Helm Chart 구성
- 배포 및 운영 가이드

---

### Phase 3: Kafka 기반 이벤트 아키텍처 전환 📡

**상태**: 예정
**구성**: Kafka + Kubernetes

```
REST API (동기) → Event-Driven (비동기)
└─ Kafka로 서비스 간 통신
   └─ 느슨한 결합 (Loose Coupling)
   └─ 개별 서비스 독립 실행 가능
```

**예정 문서**:
- Kafka 설정 가이드
- 이벤트 기반 통신 구현
- 동기→비동기 마이그레이션 전략

---

### Phase 4: Observability 구성 🔍

**상태**: 예정
**구성**: Prometheus, Grafana, Loki, Jaeger

```
완전한 모니터링 시스템
├─ Prometheus: 메트릭 수집
├─ Grafana: 대시보드
├─ Loki: 중앙 로깅
└─ Jaeger: 분산 추적
```

**예정 문서**:
- 모니터링 스택 설정
- 대시보드 구성
- 알림 규칙 설정

---

## 💡 문서 선택 팁

| 상황 | 읽어야 할 문서 |
|------|--------------|
| Java, Spring Boot 버전 확인 | ➜ 01번 |
| 서비스 구조 이해 | ➜ 02번 |
| 커밋 메시지 작성 | ➜ 03번 |
| 로컬 환경 구성 | ➜ 04번 |
| API 응답 포맷 확인 | ➜ 05번 (준비 중) |
| 데이터베이스 설계 확인 | ➜ 06번 (준비 중) |

---

## 📞 문서 수정/피드백

문서에 오류나 개선사항이 있으면:
1. GitHub Issue 생성
2. PR로 수정 요청
3. 또는 팀에 알리기

---

**마지막 업데이트**: 2026-05-17  
**버전**: 1.1
