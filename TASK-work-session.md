# 근무 세션(Work Session) 설계 문서

## 1. 배경 / 목적

- **기사 근무 단위 식별**: `ONLINE → OFFLINE` 한 번을 "근무 1건"으로 식별.
- **tracking-service 연계**: 클라이언트가 이동 기록(segment)을 tracking-service에 보낼 때 `work_session_id`를 함께 전송하기 위해 외부 노출 필요.
- **driver-service 책임 범위**: work-session 라이프사이클 관리만 담당. 실제 이동 경로(polyline/segment)는 tracking-service에서 별도 관리.

---

## 2. 결정 사항 요약

| 항목 | 결정 |
|---|---|
| 도메인 위치 | driver-service 내부 (`domain/worksession/` 별도 패키지) |
| Aggregate | WorkSession 단독 Aggregate Root |
| 식별자 외부 노출 | 노출 (클라이언트가 tracking-service 호출 시 사용) |
| 동시 IN_PROGRESS 제약 | 기사당 최대 1개 |
| 비정상 종료 정책 | 다음 ONLINE 진입 시 기존 IN_PROGRESS 강제 종료 (`endedAt = now`) 후 새 세션 시작 |
| `endReason` 컬럼 | 미도입 (사후 추론 가능, YAGNI) |
| BUSY 처리 | work-session 변화 없음 (같은 세션 내부 Driver 상태 변화) |
| 작업 단위 | driver-service 내부 작업 (tracking-service는 별도 작업) |

---

## 3. 도메인 정의

### 3.1 모델

```
WorkSession (Aggregate Root)
├── WorkSessionId       (Value Object: String UUID)
├── driverId            (String)
├── status              (WorkSessionStatus: IN_PROGRESS | ENDED)
├── startedAt           (LocalDateTime)
├── endedAt             (LocalDateTime, nullable)
├── createdAt
└── updatedAt
```

### 3.2 핵심 불변식

1. 한 기사는 `IN_PROGRESS` 세션을 동시에 1개만 보유
2. `ENDED` 세션은 재개 불가
3. `endedAt >= startedAt`

### 3.3 상태 전이

```
[없음] ── start() ──▶ IN_PROGRESS ── end() ──▶ ENDED
```

### 3.4 비정상 종료 정책

```
ONLINE 전환 요청
  ├─ 기존 IN_PROGRESS 없음 → 신규 WorkSession.start()
  └─ 기존 IN_PROGRESS 있음 → 강제 end() → 신규 WorkSession.start()
```

- 강제 종료 시점에 `endedAt = now`
- 정상/비정상 종료를 데이터로 구분하지 않음 (필요 시 tracking 데이터의 마지막 갱신 시각과 endedAt 갭으로 사후 추론)

---

## 4. 비즈니스 모델

### 4.1 유스케이스

| 액터 | 유스케이스 |
|---|---|
| 기사 (외부) | 출근/퇴근, 본인 근무 이력 조회, 현재 work-session 조회 |
| tracking-service (내부) | work_session_id 유효성 검증 |
| dispatcher-service (내부) | 배차 상태에 따른 ONLINE↔BUSY 전환 (별도 작업) |

### 4.2 Driver 상태 전환 연동

| Driver 전이 | WorkSession 동작 |
|---|---|
| OFFLINE → ONLINE | 고아 IN_PROGRESS 강제 종료 → 신규 세션 시작 |
| ONLINE → BUSY | 변화 없음 |
| BUSY → ONLINE | 변화 없음 |
| * → OFFLINE | 현재 IN_PROGRESS 세션 종료 |

→ work-session API를 별도로 노출하지 않음. 기존 `PATCH /drivers/me/status` 호출의 부수효과로 처리.

---

## 5. API 명세

### 5.1 외부 API

| API | 동작 |
|---|---|
| `PATCH /drivers/me/status` | 상태 전환 + 응답에 현재 work-session 정보 포함 |
| `GET /drivers/me/current-work-session` | 본인 현재 IN_PROGRESS 세션 조회 (없으면 404) |
| `GET /drivers/me/work-sessions` | 본인 근무 이력 (페이징, `startedAt DESC`) |
| `GET /drivers/me/work-sessions/{workSessionId}` | 본인 근무 단건 조회 |

### 5.2 내부 API

| API | 호출자 | 용도 |
|---|---|---|
| `GET /internal/work-sessions/{workSessionId}` | tracking-service | work_session_id 유효성 검증 (소속 driverId / status 확인) |

### 5.3 응답 DTO

**WorkSessionResponse**
```json
{
  "workSessionId": "uuid",
  "driverId": "uuid",
  "status": "IN_PROGRESS",
  "startedAt": "2026-06-08T05:30:00Z",
  "endedAt": null
}
```

### 5.4 에러 코드

| 코드 | HTTP | 의미 |
|---|---|---|
| `WORK_SESSION_NOT_FOUND` | 404 | work-session 미존재 |
| `WORK_SESSION_FORBIDDEN` | 403 | 타인 work-session 접근 시도 |

---

## 6. 데이터 모델

### 6.1 테이블 `work_sessions`

```sql
CREATE TABLE work_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  work_session_id VARCHAR(36) UNIQUE NOT NULL,
  driver_id VARCHAR(36) NOT NULL,
  status VARCHAR(20) NOT NULL,
  started_at TIMESTAMP NOT NULL,
  ended_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_driver_status (driver_id, status),
  INDEX idx_driver_started (driver_id, started_at)
);
```

- `idx_driver_status`: 활성 세션 조회 (`WHERE driver_id = ? AND status = 'IN_PROGRESS'`)
- `idx_driver_started`: 이력 페이징 조회 정렬

---

## 7. 패키지 구조

```
com.taxidispatcher.services.driver
├── domain/worksession/
│   ├── WorkSession.java
│   ├── WorkSessionId.java
│   ├── WorkSessionStatus.java
│   └── WorkSessionRepository.java
├── application/
│   ├── dto/response/
│   │   └── WorkSessionResponse.java
│   └── service/
│       └── WorkSessionService.java
├── infrastructure/persistence/
│   ├── WorkSessionJpaEntity.java
│   ├── WorkSessionJpaRepository.java
│   └── WorkSessionRepositoryImpl.java
└── presentation/
    ├── WorkSessionApi.java
    ├── WorkSessionController.java
    └── internal/
        ├── InternalWorkSessionApi.java
        └── InternalWorkSessionController.java
```

---

## 8. 구현 Phase + 체크리스트

### Phase 1. 스키마 + 인프라

- [ ] `infra/mysql/init-driver-schema.sql`에 `work_sessions` 테이블 추가
- [ ] (필요 시) 로컬 컨테이너 재기동으로 스키마 반영 확인

### Phase 2. 도메인 모델

- [ ] `WorkSessionId` Value Object 작성 (`generate()`, `of(String)`)
- [ ] `WorkSessionStatus` Enum 작성 (IN_PROGRESS, ENDED)
- [ ] `WorkSession` Aggregate Root 작성
  - [ ] `start(driverId)` 정적 팩토리
  - [ ] `end()` 메서드
  - [ ] `isInProgress()` 게터
- [ ] `WorkSessionRepository` 인터페이스 작성
  - [ ] `save`, `findById`, `findInProgressByDriverId`, `findByDriverId(pageable)`

### Phase 3. 인프라 (JPA)

- [ ] `WorkSessionJpaEntity` 작성 (`fromDomain`, `toDomain`, `updateFromDomain`)
- [ ] `WorkSessionJpaRepository` (Spring Data JPA)
- [ ] `WorkSessionRepositoryImpl` (도메인 ↔ JPA 변환)

### Phase 4. 애플리케이션 서비스

- [ ] `WorkSessionResponse` DTO 작성 (`from(WorkSession)`)
- [ ] `WorkSessionService` 작성
  - [ ] `startNewSession(driverId)` — 고아 강제 종료 + 신규 시작
  - [ ] `endCurrentSession(driverId)` — 현재 IN_PROGRESS 종료
  - [ ] `findById(workSessionId)` — 내부 API용
  - [ ] `findCurrentByDriverId(driverId)` — 본인 현재 세션
  - [ ] `findHistoryByDriverId(driverId, pageable)` — 본인 이력
- [ ] `DriverService.changeStatus()` 흐름에 `WorkSessionService` 호출 삽입
  - [ ] OFFLINE → ONLINE: `startNewSession`
  - [ ] * → OFFLINE: `endCurrentSession`
  - [ ] ONLINE ↔ BUSY: 호출 없음

### Phase 5. 외부 API

- [ ] `WorkSessionApi` 인터페이스 (Swagger 명세)
- [ ] `WorkSessionController` 구현
  - [ ] `GET /drivers/me/current-work-session`
  - [ ] `GET /drivers/me/work-sessions` (페이징)
  - [ ] `GET /drivers/me/work-sessions/{workSessionId}` (본인 소유 검증)
- [ ] `PATCH /drivers/me/status` 응답에 work-session 정보 노출 (※ 응답 통합 방식은 §10 보류 항목, 결정 후 적용)

### Phase 6. 내부 API

- [ ] `InternalWorkSessionApi` 인터페이스 (ApiKey 인증)
- [ ] `InternalWorkSessionController` 구현
  - [ ] `GET /internal/work-sessions/{workSessionId}`

### Phase 7. 예외 / 통합 검증

- [ ] 에러 코드 추가 (`WORK_SESSION_NOT_FOUND`, `WORK_SESSION_FORBIDDEN`)
- [ ] `GlobalExceptionHandler` 처리 확인
- [ ] 본인 소유 검증 로직 (JWT의 driverId vs 조회 대상 workSession.driverId)
- [ ] 시나리오 검증
  - [ ] OFFLINE → ONLINE → 새 세션 생성
  - [ ] ONLINE → OFFLINE → 세션 종료
  - [ ] ONLINE → BUSY → ONLINE → 세션 유지
  - [ ] 고아 IN_PROGRESS 상태에서 ONLINE 재진입 → 기존 강제 종료 + 신규 생성
  - [ ] tracking-service가 internal API로 work_session_id 조회 시 정상 응답

---

## 9. SecurityConfig 영향

신규 추가 경로 인증 정책:

| 경로 | 인증 |
|---|---|
| `/drivers/me/current-work-session` | JWT |
| `/drivers/me/work-sessions/**` | JWT |
| `/internal/work-sessions/**` | ApiKey |

→ 기존 `DriverSecurityConfig`의 `getAdditionalPaths()` 영향 없음 (이미 `/drivers/**`, `/internal/**` 패턴으로 커버됨, 확인 필요).

---

## 10. 보류 / 추후 결정 항목

| 항목 | 내용 |
|---|---|
| 응답 통합 방식 | `PATCH /drivers/me/status` 응답에 work-session을 (a) `DriverResponse`에 필드로 통합 vs (b) 별도 응답 DTO로 묶기. 구현 직전 결정 |
| `GET /drivers/me` 응답 일관성 | (a)로 결정 시 기존 조회 API에도 currentWorkSession이 노출되어야 함. 통합 결정 후 함께 처리 |
| 비정상 종료 메트릭 | `endReason` 컬럼 추가는 미도입. 향후 운영 분석 요구 발생 시 재검토 |
| dispatcher-service ↔ driver-service BUSY 전환 internal API | 본 문서 범위 외 (별도 작업) |
| tracking-service 구현 | 본 문서 범위 외 (별도 서비스 작업) |
