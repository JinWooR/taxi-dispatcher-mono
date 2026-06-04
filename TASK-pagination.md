# 페이지네이션 & 정렬 & 날짜 검색 표준화 작업 계획서

## 배경

현재 컨트롤러에서 Spring Data의 `Pageable`을 직접 노출하고 있어 다음 문제가 발생.

- 계층 누수 (인프라 객체가 컨트롤러까지 노출)
- 정렬 가능 필드 검증 부재 → 임의 필드 정렬 시도 가능 (보안/성능 위험)
- API 명세 모호 (Swagger 자동 노출되나 유효 옵션 불명확)
- size 상한 제약 부재
- 날짜 검색 패턴 표준화 부재
- 글로벌 시간대 대응 부재 (LocalDateTime은 시간대 정보 없음)

## 목표

- 공통 DTO 기반 페이지네이션/정렬 표준화
- 정렬 필드 화이트리스트 강제
- 글로벌 시간대 대응 (Instant 입출력)
- 모든 서비스에 일관된 패턴 적용

## 의존성 정책

- **`shared/common-lib`는 가벼움 유지**
- 허용: **API 스펙성 의존성** (어노테이션/인터페이스 위주), Java 표준 라이브러리
  - 예: `jakarta.validation-api` (검증 어노테이션)
- 금지: 구현체 의존성 (Hibernate Validator, Spring Data 등)
- 의존성이 발생하는 변환 로직(Spring Data)만 각 서비스의 utility로 분리

## 시간 타입 전략

| 계층 | 타입 | 시간대 약속 |
|------|------|------------|
| API 입출력 (DTO) | `Instant` | UTC 절대 시각 (ISO 8601 자동 처리) |
| Application/Domain | `LocalDateTime` | UTC 기준 |
| JPA Entity / DB | `LocalDateTime` | UTC 기준 (`hibernate.jdbc.time_zone: UTC`) |

**변환 규칙:**
```
Instant → LocalDateTime: LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
LocalDateTime → Instant: ldt.atOffset(ZoneOffset.UTC).toInstant()
```

## 설계 방향

### 1. PageableRequest (common-lib, 순수 DTO)

```
page    : int           (default 0, @Min(0))
size    : int           (default 20, @Min(1), @Max(100))
sort    : List<String>  (예: ["requestedAt,desc", "status,asc"])
```

- 정렬 표현: `field,direction` (Spring Data 표준 패턴)
- URL 안전성 확보 (+/- 접두사 회피)
- 변환 메서드 없음 (Spring Data 의존성 회피)
- 검증 어노테이션: `jakarta.validation-api`

### 2. DateRangeRequest (common-lib, 순수 DTO)

```
startDate : Instant   (ISO 8601, UTC 절대 시각)
endDate   : Instant   (ISO 8601, UTC 절대 시각)
```

- 클라이언트 입력 예: `"2026-06-04T14:30:00+09:00"`, `"2026-06-04T05:30:00Z"`
- Jackson이 ISO 8601 → Instant 자동 파싱 (시간대 정보 활용)
- 검색 범위는 클라이언트가 정확히 명시

### 3. TimeConverter (common-lib, static utility)

```
위치: shared/common-lib/.../common/util/TimeConverter.java

public static LocalDateTime toUtcLocalDateTime(Instant instant)
public static Instant toInstant(LocalDateTime utcLdt)
```

- **Java 표준 라이브러리만 사용** → common-lib에 통합
- 모든 서비스가 동일 변환 로직 사용 (UTC 약속 일관성)

### 4. PageableConverter (각 서비스, static utility)

```
위치: services/{service}-service/.../util/PageableConverter.java

public static Pageable toPageable(PageableRequest request, Set<String> allowedFields)
```

- **Spring Data 의존**으로 common-lib 외부에 위치
- 화이트리스트 외 필드 → `IllegalArgumentException`
- `field,direction` 콤마 구분 파싱

### 5. 정렬 화이트리스트

- 각 서비스의 컨트롤러/Service 레이어에서 `Set<String>` 또는 enum으로 API별 정의

## 작업 범위 & 체크리스트

### Phase 1: shared/common-lib

- [x] `jakarta.validation-api` 의존성 추가
- [x] `PageableRequest`, `DateRangeRequest` DTO 생성
- [x] `TimeConverter` utility 생성

### Phase 2: dispatcher-service (선행 검증)

- [ ] `PageableConverter` utility 생성
- [ ] 페이징 API 시그니처 변경 (`Pageable` → `PageableRequest`)
- [ ] 정렬 화이트리스트 정의 및 적용

### Phase 3: driver-service / user-service / account-service

- [ ] 각 서비스에 `PageableConverter` 추가
- [ ] 페이징 API 시그니처 변경 및 화이트리스트 적용

### Phase 4: 공통 문서 갱신

- [ ] `docs/05-api-common-rules.md`에 페이지네이션/정렬/날짜 표준 추가

## 마이그레이션 순서

```
1. common-lib (DTO + TimeConverter)
   ↓
2. dispatcher-service (PageableConverter 검증)
   ↓
3. driver / user / account-service 순차 적용
   ↓
4. 공통 문서 갱신
```

## 검증 사항

- [ ] 화이트리스트 외 필드 정렬 시 400 응답
- [ ] size 상한 초과 시 400 응답
- [ ] 다양한 시간대 입력(`+09:00`, `Z`, `-04:00`) → 동일 UTC 시각 변환 확인
- [ ] 응답 Instant → ISO 8601 (Z) 형식 확인
- [ ] common-lib 의존성 점검 (Spring Data 미포함)

## 의사결정 기록

- **정렬 표현 방식**: `field,direction` 콤마 구분 채택
  - `+/-` 접두사는 URL 인코딩 이슈로 미채택 (`+`가 공백으로 디코딩)
- **common-lib 정책**: 가벼움 유지, API 스펙성 의존성(`jakarta.validation-api`)만 허용
  - Spring Data 등 구현체 의존성은 추가 안 함
- **변환 로직 위치**:
  - `TimeConverter` → common-lib (Java 표준만 사용, 의존성 부담 없음)
  - `PageableConverter` → 각 서비스 (Spring Data 의존)
- **시간 타입 전략**: API는 `Instant`, 내부/JPA는 `LocalDateTime` (UTC 약속)
- **날짜 정밀도(DatePrecision) 제외**:
  - Instant + ISO 8601이 이미 표준 → 추가 메타데이터 불필요
  - 검색 범위는 클라이언트가 명확히 명시 (책임 분리)
- **시간대 기준**: UTC + ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss'Z'`) 일관 유지

## 비고

- 본 작업은 기존 동작 변경 (Breaking Change) → API 사용 측 영향 사전 공지 필요
- LocalDateTime은 항상 UTC 기준 약속 (도메인/JPA/DB 모두)
- `hibernate.jdbc.time_zone: UTC` 설정 필수 (이미 적용됨)
