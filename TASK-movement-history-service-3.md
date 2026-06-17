# movement-history-service 좌표 누적 모델 전환 계획

> 본 문서는 **향후 전환 계획** 입니다. 현재 구현 상태는 [`TASK-movement-history-service-2.md`](docs/task/TASK-movement-history-service-2.md) 참조.

---

## 1. 배경

현재 PUT 갱신은 polyline 덮어쓰기 → 단 1회 잘못된 호출로 segment 전체 데이터 손실 가능. 추적·복구 불가.

목표: **좌표 raw 누적 + 서버 인코딩** 모델로 전환하여 추적성·무결성 확보.

---

## 2. 모델 비교

| 항목 | 현재 | 목표 |
|---|---|---|
| 인코딩 책임 | 클라이언트 | 서버 |
| PUT 동작 | polyline 덮어쓰기 | 좌표 list append |
| IN_PROGRESS 보관 | polyline | 좌표 raw (`movement_segment_points`) |
| COMPLETED 보관 | polyline | polyline (인코딩 결과) |
| 추적성 | 약함 | 강함 (좌표 단위) |

---

## 3. 주요 변경

**DB**
- `movement_segments.polyline` NULL 허용 (IN_PROGRESS 시 null)
- 신규 테이블 `movement_segment_points (id, segment_id, sequence_no, latitude, longitude, recorded_at)`
- COMPLETED 후 좌표 row 삭제 → 영구 보관량 절감

**API**
- `PUT .../segments/{id}` → `POST .../segments/{id}/points` (좌표 list append)
- `POST .../segments/complete` → 좌표 → polyline 인코딩 후 저장
- `POST .../segments/rotate` → current/next 모두 좌표 list 로 변경
- `POST .../segments` (start) → 첫 좌표 list 로 변경

**서버**
- Google Polyline Algorithm (precision 5) 인코더 자체 구현 또는 라이브러리 도입

---

## 4. 전환 시 의사결정 사항

| # | 항목 | 추천 |
|---|---|---|
| 1 | IN_PROGRESS 좌표 보관 | 별도 테이블 (`movement_segment_points`) |
| 2 | COMPLETED 후 좌표 row | 삭제 |
| 3 | 좌표 정밀도 | DOUBLE (기존 컨벤션) |
| 4 | `recordedAt` 필수 여부 | 필수 (클라이언트 측 시각 보존) |
| 5 | 인코더 | 자체 구현 (~50줄, 외부 의존성 회피) |

---

## 5. 관련 코드 TODO

[`MovementSegmentController.updateSegmentPolyline`](services/movement-history-service/src/main/java/com/taxidispatcher/services/movementhistory/presentation/MovementSegmentController.java) 메서드 위 TODO 주석에 본 문서 링크 명시. 전환 작업 진입 시 시작점.

---

## 6. 변경 이력

| 일자 | 변경 |
|---|---|
| 2026-06-16 | 좌표 누적 모델 전환 계획 (-3) 작성 |
