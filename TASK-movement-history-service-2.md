# movement-history-service 구현 명세

> ✅ 본 문서는 **실제 코드 기준 최신 명세** 입니다.
> 초기 설계 / 결정 이력은 [`TASK-movement-history-service-1.md`](TASK-movement-history-service-1.md) 를 참조하세요.  
> 향후 전환 계획 (좌표 누적 모델) 은 [`TASK-movement-history-service-3.md`](TASK-movement-history-service-3.md) 를 참조하세요.

---

## 1. 개요 및 초기 설계 대비 변경 요약

| 항목 | 초기 (-1) | 구현 (-2) | 사유 |
|---|---|---|---|
| 보관 모델 | 단일 테이블 + `context_type` polymorphic | `context_type` 제거, `work_session_id` 항상 + `dispatch_id` nullable | work_session 단일 도메인 + segment 가 dispatch 식별자 동봉 |
| segment PK 타입 | VARCHAR(36) UUID | BIGINT auto increment | 대량 데이터, 단일 DB, 학습 프로젝트 한정 의식적 결정 |
| `customer_id` 컬럼 | DISPATCH_TRIP 시 보관 | **미보관** | 1차 customer 권한 검증 약화 의식적 수용 |
| `segment_no` | 클라이언트 전송 | **서버 자동 할당** (count + 1) | 클라이언트 부담 감소, UNIQUE 안전망 |
| DISPATCH_TRIP CRUD | POST/PUT/finalize/GET | **조회 only** (`GET /dispatches/{id}`) | work_session 단일 관리 |
| §6 lifecycle finalize 동기화 | 결정 보류 | **(a) 클라이언트 주도** 확정 | 1차 단순화, internal API 보완은 후속 |
| segment 전환 (rotate) | 미정의 | **POST `.../segments/rotate`** 추가 | 10분 단위 분리 시 클라이언트 1회 호출 |
| 활성 segment 조회 | 미정의 | **GET `/drivers/me/active-segment`** 추가 | 클라이언트 segment_id 유실 복구용 |
| 기간별 조회 | 미정의 | **GET `/drivers/me?startDate&endDate`** 추가 | 기사 본인 이력 조회 |

---

## 2. 도메인 모델

### 2.1 enum

```
MovementSegmentStatus  : IN_PROGRESS  →  COMPLETED   (단방향, COMPLETED 불변)
```

### 2.2 테이블 `movement_segments` (DB: `movement_history_db`)

| 컬럼 | 타입 | 제약 | 비고 |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Segment PK |
| `work_session_id` | VARCHAR(36) | NOT NULL | 근무 세션 ID (driver-service 참조) |
| `driver_id` | VARCHAR(36) | NOT NULL | 기사 ID |
| `dispatch_id` | VARCHAR(36) | NULL | 배차 운행 중 segment 만 |
| `segment_no` | INT | NOT NULL | 1부터, 서버 자동 할당 |
| `polyline` | TEXT | NOT NULL | Google Encoded Polyline (precision 5) |
| `status` | VARCHAR(20) | NOT NULL | enum |
| `started_at` | DATETIME(6) | NOT NULL | 시작 시각 (UTC) |
| `ended_at` | DATETIME(6) | NULL | 종료 시각 (UTC), IN_PROGRESS 시 null |
| `updated_at` | DATETIME(6) | NOT NULL | 갱신/완료 시각 (UTC) |

### 2.3 인덱스

```
UNIQUE KEY uk_session_segment (work_session_id, segment_no)
INDEX      idx_driver_started  (driver_id, started_at)
INDEX      idx_dispatch_id     (dispatch_id)
INDEX      idx_status          (status)
```

### 2.4 상태 전이

```
start(...)            → status = IN_PROGRESS
updatePolyline(...)   → IN_PROGRESS 에서만 허용, polyline 덮어쓰기
complete(...)         → IN_PROGRESS → COMPLETED, ended_at 확정
```

- 위반 시 `DomainException("MOVEMENT_SEGMENT_COMPLETED_NOT_EDITABLE" / "MOVEMENT_SEGMENT_INVALID_TRANSITION", HTTP 409)`
- polyline 은 값 객체 `EncodedPolyline` 으로 감싸 null/empty 검증

---

## 3. API 명세

모든 API: `/api/movements/...`, JWT Bearer 인증.

### 3.1 WORK_SESSION CRUD (driver-only)

| Method | Path | 용도 |
|---|---|---|
| POST | `/work-sessions/{workSessionId}/segments` | 새 segment 시작 (segmentNo 서버 자동) |
| POST | `/work-sessions/{workSessionId}/segments/rotate` | 활성 segment complete + 새 segment 시작 (1회 호출) |
| PUT | `/work-sessions/{workSessionId}/segments/{segmentId}` | 진행 중 segment polyline 갱신 |
| POST | `/work-sessions/{workSessionId}/segments/complete` | 활성 segment 완료 (자동 식별, 근무 종료 시) |
| GET | `/work-sessions/{workSessionId}` | 근무 단위 segment 전체 조회 |

### 3.2 DISPATCH_TRIP 조회 (driver + customer)

| Method | Path | 권한 | 용도 |
|---|---|---|---|
| GET | `/dispatches/{dispatchId}` | DRIVER / CUSTOMER | 배차 단위 segment 조회 |

- driver 토큰: segment.driver_id 와 토큰 actor 일치 검증
- customer 토큰: **검증 없음** (1차 의식적 수용, 보안 보완 후순위)

### 3.3 기사 본인 조회 (driver-only)

| Method | Path | 용도 |
|---|---|---|
| GET | `/drivers/me?startDate&endDate` | 기간별 segment 조회 (UTC `Instant`, 필수) |
| GET | `/drivers/me/active-segment` | 현재 진행 중 segment 1개 (없으면 `data: null`, segment_id 유실 복구용) |

### 3.4 Request 본문 — `StartWorkSessionSegmentRequest`

```json
{
  "polyline": "u{~vFvyys@fS]",
  "dispatchId": "550e8400-...?"
}
```

> `segmentNo` 는 서버 자동 할당 (전송 X)

### 3.5 rotate API 동작 정책

- 활성 segment 있으면: 본인 driver 소유 검증 → complete → 새 segment 시작 (단일 트랜잭션)
- 활성 segment 없으면: 그냥 새 시작 (= POST `.../segments` 와 동일) — **관대 모드**
- 클라이언트가 첫 segment 부터 항상 rotate 만 호출해도 동작 OK

---

## 4. 서비스 간 연계

- **driver-service**: `work_session` 도메인 보유. **새 segment 생성 시** (start, rotate-without-active) `InternalWorkSessionApi.findById` (`GET /internal/work-sessions/{workSessionId}`) 호출로 검증 — 존재 여부 / driverId 일치 / status IN_PROGRESS. rotate 의 활성 segment 있음 분기는 검증 skip (이미 검증된 세션의 연장).
- **dispatcher-service**: `Dispatch` 도메인 보유. 본 서비스는 `dispatch_id` 참조만.
- **운행 시작/종료 시 segment finalize**: §6 결정에 따라 **클라이언트 주도**. 서버간 internal 호출 없음.

---

## 5. 보안 / 권한 정책

| API | 권한 | 추가 검증 |
|---|---|---|
| WORK_SESSION CRUD | `hasRole('DRIVER')` | Service 에서 segment.driverId == 토큰 actor 검증 |
| DISPATCH_TRIP 조회 | `hasAnyRole('DRIVER', 'CUSTOMER')` | driver 만 driverId 일치 검증, customer 검증 없음 (1차 수용) |
| 기사 본인 조회 | `hasRole('DRIVER')` | 토큰 actor → driverId |

- 권한 검증 실패: `DomainException("FORBIDDEN", HTTP 403)`
- 도메인 상태 위반: `DomainException("MOVEMENT_SEGMENT_*", HTTP 409)`
- 미인증: `BaseSecurityConfig` 가 401

---

## 6. 보류 / 후속 작업

- **좌표 누적 모델 전환** (polyline 덮어쓰기 → 좌표 list append + 서버 인코딩) — 상세 계획 [`TASK-movement-history-service-3.md`](TASK-movement-history-service-3.md). 현재는 의식적으로 1차 모델 유지
- **DTO record 마이그레이션 (점진적)** — 신규 DTO 부터 record 도입 (`RotateSegmentRequest` 가 시작점). 추후 기존 DTO (Request/Response) 도 record 마이그레이션 예정. 전사적 컨벤션 확정 시 다른 서비스도 함께 전환
- **§6 lifecycle finalize internal API 보완** — (a) 클라이언트 주도만으로 강제 종료 시 누락 위험. 후속에 driver/dispatcher → movement-history internal API 추가 검토
- **DISPATCH_TRIP customer 권한 검증 강화** — 현재 customer 토큰 단순 조회. 향후 segment 에 customer_id 컬럼 추가 또는 dispatcher-service internal 호출 도입
- **동시 start race condition** — UNIQUE `(work_session_id, segment_no)` 안전망. 명시적 처리 없음 (실용 빈도 낮음)
- **메트릭 산출** (총 거리/시간) — 클라이언트 vs 서버 미결
- **폴리라인 단순화** (Douglas-Peucker) — 미적용
- **실시간 위치 조회 API** — 본 문서 범위 외
- **cold storage 전환** — 영구 보관 전제, 분리 시점 추후

---

## 7. 구현 Phase 체크리스트

- [x] **Phase 1**: 서비스 모듈 초기 세팅 (Spring Boot + docker-compose + 환경 변수 + MySQL init schema)
- [x] **Phase 2**: 도메인 모델 (`MovementSegment` + value objects + Repository port + JPA persistence)
- [x] **Phase 3**: Presentation 계층 WORK_SESSION CRUD (Security/OpenApi 포함, Api 명세/Controller 매핑 분리)
- [x] **Phase 3.1**: HV000151 픽스 (Api 인터페이스에 `@Valid` 명시)
- [x] **Phase 4**: 후속 API (DISPATCH_TRIP 조회 / 기간별 me 조회 / 활성 segment 조회)
- [x] **Phase 5**: segmentNo 자동 할당 + rotate API
- [ ] **Phase 6**: 통합 검증 (docker-compose → Swagger → 8개 API 호출)
- [ ] **Phase 7 (후속)**: lifecycle finalize internal API 보완 (필요 시)
- [ ] **Phase 7 (후속)**: DISPATCH_TRIP customer 권한 검증 강화 (필요 시)
- [ ] **Phase 7 (후속)**: driver-service `work_session` 도메인 추가 (선행 서비스 측 작업)

---

## 8. 변경 이력

| 일자 | 변경 |
|---|---|
| 2026-06-08 | 초기 설계 문서 (-1) 작성 |
| 2026-06-12 | 구현 명세 문서 (-2) 신설 + 초기 설계와 양방향 링크. segmentNo 자동 할당, rotate API, DISPATCH_TRIP 단순화, 후속 결정 일괄 반영 |
| 2026-06-16 | complete API 에서 `segmentId` 제거 (활성 자동 식별). `MOVEMENT_NO_ACTIVE_SEGMENT` (404) 에러 추가. rotate/start 와 패턴 일관화 |
| 2026-06-16 | rotate API body 를 `{ current, next }` 구조로 분리 (current.polyline 으로 마지막 좌표 보강 가능) |
| 2026-06-16 | 좌표 누적 모델 전환 계획 (-3) 작성. 현 모델은 유지, 추후 전환 예정 |
| 2026-06-16 | `RotateSegmentRequest` 를 record 로 전환 + 기존 DTO (`UpdateSegmentPolylineRequest`, `StartWorkSessionSegmentRequest`) 재사용. DTO record 마이그레이션 점진적 시작점 |
| 2026-06-16 | 새 segment 생성 시 driver-service `InternalWorkSessionApi.findById` 로 work_session 검증 추가 (`MOVEMENT_WORK_SESSION_NOT_FOUND` 404 / `FORBIDDEN` 403 / `MOVEMENT_WORK_SESSION_NOT_IN_PROGRESS` 409). rotate 의 활성 있음 분기는 검증 skip |
