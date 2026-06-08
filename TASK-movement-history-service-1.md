# movement-history-service 설계 문서 (초기)

> ⚠️ 본 문서는 **초기 설계 단계의 결정 이력 보존용** 입니다. 일부 결정 사항은 후속 작업에서 변경됐습니다.
> 최신 구현 명세는 [`TASK-movement-history-service-2.md`](TASK-movement-history-service-2.md) 를 참조하세요.

---

## 1. 배경 / 목적

- 기사 운영 분석(근무 단위 이동)과 배차 영수증/분쟁 대응(배차 단위 이동)을 위한 **이동 이력 보관 전용** 마이크로서비스.
- driver-service / dispatcher-service 는 **lifecycle 만 책임**, 이동 segment 보관은 본 서비스가 단독 책임.
- 두 단위(근무·배차)의 이동 segment 를 **단일 도메인(movement) 으로 통합 관리**, 외부 식별자(`work_session_id`, `dispatch_id`, `driver_id`, `customer_id`) 는 연관 참조용으로 함께 보관.

---

## 2. 결정 사항 요약

| 항목 | 결정 |
|---|---|
| 보관 단위 | 단일 테이블 `movement_segments` + `context_type` 컬럼 |
| 좌표 압축 | Google Encoded Polyline (precision = 5) |
| 적재 방식 | 10분 단위 segment 분리, 진행중 segment 는 polyline 덮어쓰기 update |
| 좌표 수집 주체 | 기사 클라이언트 (앱) |
| 좌표 수집 주기 | 10초 (프론트 결정) |
| ID 타입 | VARCHAR(36) UUID (기존 서비스 컨벤션) |
| nullable 정책 | NULL 그대로 보관 (sentinel 값 사용 X) |
| 보관 기간 | 영구 (cold storage 전환은 추후) |
| 작업 의존 | driver-service `work_session` 도메인 선행 필요 |

---

## 3. 도메인 모델

### 3.1 enum

```
MovementContextType    : WORK_SESSION | DISPATCH_TRIP
MovementSegmentStatus  : IN_PROGRESS  → COMPLETED  (단방향, COMPLETED 불변)
```

### 3.2 테이블 `movement_segments`

| 컬럼 | 타입 | 제약 | 비고 |
|---|---|---|---|
| `segment_id` | VARCHAR(36) | PK | UUID |
| `context_type` | VARCHAR(20) | NOT NULL | enum |
| `context_id` | VARCHAR(36) | NOT NULL | work_session_id / dispatch_id |
| `driver_id` | VARCHAR(36) | NOT NULL | 항상 존재 |
| `customer_id` | VARCHAR(36) | NULL | `DISPATCH_TRIP` 일 때만 |
| `segment_no` | INT | NOT NULL | 1, 2, 3, ... |
| `polyline` | TEXT | NOT NULL | encoded (precision 5) |
| `status` | VARCHAR(20) | NOT NULL | enum |
| `started_at` | DATETIME(6) | NOT NULL | segment 시작 시각 (= 생성 시각) |
| `ended_at` | DATETIME(6) | NULL | `IN_PROGRESS` 일 때 null |
| `updated_at` | DATETIME(6) | NOT NULL | polyline 갱신 / finalize 시각 |

### 3.3 인덱스

```
UNIQUE KEY uk_context_segment (context_type, context_id, segment_no)
INDEX      idx_driver_started  (driver_id, started_at)
INDEX      idx_context_status  (context_type, status)
```

### 3.4 상태 전이

```
start(...)           → status = IN_PROGRESS
updatePolyline(...)  → IN_PROGRESS 에서만 허용, polyline 덮어쓰기
finalize(...)        → IN_PROGRESS → COMPLETED, ended_at 확정
```

- `customer_id` 는 `context_type == DISPATCH_TRIP` 일 때만 non-null (도메인 검증).
- polyline 은 값 객체 `EncodedPolyline` 으로 감싸 precision/형식 검증.

---

## 4. API 명세 (context 별 경로 분리, 단일 테이블)

### 4.1 근무 단위 (WORK_SESSION)

| Method | Path | 용도 |
|---|---|---|
| POST | `/api/movements/work-sessions/{workSessionId}/segments` | 새 segment 시작 |
| PUT  | `/api/movements/work-sessions/{workSessionId}/segments/{segmentId}` | 진행중 segment polyline 갱신 |
| POST | `/api/movements/work-sessions/{workSessionId}/segments/{segmentId}/finalize` | segment 종료 |
| GET  | `/api/movements/work-sessions/{workSessionId}` | 근무 단위 전체 경로 조회 |

### 4.2 배차 단위 (DISPATCH_TRIP)

| Method | Path | 용도 |
|---|---|---|
| POST | `/api/movements/dispatches/{dispatchId}/segments` | 새 segment 시작 |
| PUT  | `/api/movements/dispatches/{dispatchId}/segments/{segmentId}` | 진행중 segment polyline 갱신 |
| POST | `/api/movements/dispatches/{dispatchId}/segments/{segmentId}/finalize` | segment 종료 |
| GET  | `/api/movements/dispatches/{dispatchId}` | 배차 단위 전체 경로 조회 |

> 내부 저장은 단일 `movement_segments`, 외부 API 는 context 별로 분리하여 권한/의미 명확화.

---

## 5. 서비스 간 연계

- **driver-service**: `work_session` 도메인 보유(ONLINE~OFFLINE lifecycle). movement-history-service 는 `work_session_id` 만 참조.
- **dispatcher-service**: `Dispatch` 도메인 보유. 운행 lifecycle 은 실제 코드 enum 기준으로 **`IN_PROGRESS ~ COMPLETED`** 구간이 배차 이동.
  - 참고: 실제 `DispatchStatus` 는 `REQUESTED → ASSIGNED → IN_PROGRESS → ARRIVED → COMPLETED`. 별도 `PICKED_UP` 신규 추가 불필요.
- movement-history-service 는 외부 lifecycle 에 관여하지 않으며, 종료 시 finalize 동기화 방식은 §6 참조.

---

## 6. lifecycle 종료 시 segment finalize 동기화 (결정 보류)

기사 `OFFLINE` 또는 dispatch `COMPLETED` 진입 시 진행중 segment 자동 finalize 가 필요. 주체는 미정.

| 옵션 | 장점 | 단점 |
|---|---|---|
| (a) 클라이언트 주도 | 서비스 결합도 낮음 | 앱 강제종료 시 finalize 누락 위험 |
| (b) 서버간 internal 호출 | 누락 없음, 일관성 보장 | driver/dispatcher → movement-history 결합 추가 |

→ 별도 결정 항목.

---

## 7. 구현 Phase

| Phase | 범위 |
|---|---|
| Phase 1 | 서비스 스캐폴딩 (build.gradle, Application, application.yml, Dockerfile) |
| Phase 2 | 도메인 모델 + JPA entity + Repository |
| Phase 3 | API (context 별 경로 분리, Swagger) + 도메인 상태 전이 검증 |
| Phase 4 | driver-service `work_session` 도메인 추가 (선행 작업) |
| Phase 5 | lifecycle finalize 동기화 방식 결정·구현 (§6) |
| Phase 6 | 통합 검증 (polyline 라운드트립, 동시성, 종료 시 자동 finalize) |

---

## 8. 보류 / 미결 사항

- **lifecycle finalize 동기화 방식** (§6)
- **`customer_id` 보관 여부** — 고객 관점 조회 수요 확인 후 제거 가능
- **메트릭(총 거리/시간) 산출 주체** — 클라이언트 vs 서버
- **상위 메타 테이블(`movement_contexts`) 도입 여부** — 현재 미도입, 메트릭/전체 status 요구 시 검토
- **폴리라인 단순화(Douglas-Peucker) 클라이언트 적용 여부**
- **실시간 위치 조회 API** — 본 문서 범위 외
- **cold storage 전환 전략** — 영구 보관 전제, 분리 시점 추후
