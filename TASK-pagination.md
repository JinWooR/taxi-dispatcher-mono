# 페이지네이션 & 정렬 & 날짜 검색 표준화 작업 계획서

## 배경

현재 컨트롤러에서 Spring Data의 `Pageable`을 직접 노출하고 있어 다음 문제가 발생.

- 계층 누수 (인프라 객체가 컨트롤러까지 노출)
- 정렬 가능 필드 검증 부재 → 임의 필드 정렬 시도 가능 (보안/성능 위험)
- API 명세 모호 (Swagger 자동 노출되나 유효 옵션 불명확)
- size 상한 제약 부재
- 날짜 검색 패턴 표준화 부재

## 목표

- 공통 DTO 기반 페이지네이션/정렬 표준화
- 정렬 필드 화이트리스트 강제
- 날짜 검색 정밀도 옵션 표준화 (UTC + ISO 8601 기준)
- 모든 서비스에 일관된 패턴 적용

## 의존성 정책

- **`shared/common-lib`는 가벼움 유지**: Spring Data 의존성 추가하지 않음
- common-lib에는 순수 DTO(필드만 가진 데이터 구조)만 위치
- `Pageable` 변환 로직은 각 서비스의 utility 클래스에 위치 (각 서비스의 Spring Data 의존성 사용)

## 설계 방향

### 1. PageableRequest (common-lib, 순수 DTO)

```
page    : int    (default 0, @Min(0))
size    : int    (default 20, @Min(1), @Max(100))
sort    : List<String>  (예: ["requestedAt,desc", "status,asc"])
```

- 정렬 표현: `field,direction` (Spring Data 표준 패턴)
- URL 안전성 확보 (+/- 접두사 회피)
- **변환 메서드 없음** (Spring Data 의존성 회피)

### 2. DateRangeRequest (common-lib, 순수 DTO)

```
startDate : LocalDateTime (UTC)
endDate   : LocalDateTime (UTC)
precision : DatePrecision (YEAR | MONTH | DAY | HOUR | MINUTE | SECOND)
```

- 시간대: UTC 기준 (프로젝트 공통 규칙 `docs/05-api-common-rules.md`)
- precision으로 클라이언트 검색 정밀도 표현

### 3. PageableConverter (각 서비스의 static utility)

각 서비스의 `infrastructure/util` 또는 `application/util` 패키지에 위치.

```
위치 (서비스별):
  services/{service}-service/.../util/PageableConverter.java

public static Pageable toPageable(PageableRequest request, Set<String> allowedFields)
public static Sort.Order parseOrder(String token, Set<String> allowedFields)
```

- 화이트리스트에 없는 필드 → `IllegalArgumentException`
- `field,direction` 콤마 구분 파싱
- direction 미지정 시 ASC 기본값

### 4. 정렬 화이트리스트 정의 위치

- 각 서비스의 컨트롤러/Service 레이어에서 `Set<String>` 또는 enum으로 정의
- API별로 허용 필드가 다르므로 API별 정의

## 작업 범위

### Phase 1: 공통 라이브러리 (shared/common-lib)

- [ ] `PageableRequest` 순수 DTO 생성
  - 위치: `shared/common-lib/.../common/request/PageableRequest.java`
  - `page`, `size`, `sort` 필드만 보유
  - 검증 어노테이션 (`@Min`, `@Max`)
  - Spring Data 의존성 없음
- [ ] `DateRangeRequest` 순수 DTO 생성
  - 위치: `shared/common-lib/.../common/request/DateRangeRequest.java`
  - `DatePrecision` enum (YEAR ~ SECOND)
  - UTC 기준 LocalDateTime 사용

### Phase 2: account-service 적용

- [ ] `PageableConverter` static utility 생성
  - 위치: `services/account-service/.../infrastructure/util/PageableConverter.java`
- [ ] 페이징 사용 API 식별
- [ ] 정렬 화이트리스트 정의 (API별)
- [ ] 컨트롤러/Api 인터페이스 시그니처 변경 (`Pageable` → `PageableRequest`)
- [ ] Service 레이어에서 `PageableConverter.toPageable(req, allowedFields)` 호출
- [ ] Swagger 문서 갱신

### Phase 3: user-service 적용

- [ ] `PageableConverter` static utility 생성
- [ ] 페이징 사용 API 식별
- [ ] 정렬 화이트리스트 정의
- [ ] 컨트롤러/Api 인터페이스 변경
- [ ] Service 레이어 변환 로직 적용
- [ ] Swagger 문서 갱신

### Phase 4: driver-service 적용

- [ ] `PageableConverter` static utility 생성
- [ ] 페이징 사용 API 식별 (위치 이력 등)
- [ ] 정렬 화이트리스트 정의
- [ ] 컨트롤러/Api 인터페이스 변경
- [ ] Service 레이어 변환 로직 적용
- [ ] Swagger 문서 갱신

### Phase 5: dispatcher-service 적용

- [ ] `PageableConverter` static utility 생성
- [ ] 정렬 화이트리스트 정의
  - 고객 배차 목록: `requestedAt`, `dispatchStatus`
  - 기사 pending 배차: `requestedAt`, `scopeStartedAt`
- [ ] `DispatchController` 시그니처 변경
  - `getMyDispatches`
  - `getPendingDispatches`
- [ ] `DispatchApi` (Swagger 인터페이스) 시그니처 변경
- [ ] `DispatchService` 메서드 시그니처 변경
- [ ] `DispatchServiceImpl` 변환 로직 적용

### Phase 6: 공통 문서 갱신

- [ ] `docs/05-api-common-rules.md`에 페이지네이션/정렬/날짜 검색 표준 추가
  - 정렬 표현 (`field,direction`)
  - size 상한 제약
  - 날짜 정밀도 enum
  - UTC 기준 명시
  - common-lib 의존성 정책 명시

## 마이그레이션 순서

```
1. common-lib (순수 DTO 생성)
   ↓
2. dispatcher-service (PageableConverter 검증, 정렬 가능 API 다수)
   ↓
3. driver-service / user-service / account-service (순차 적용)
   ↓
4. 공통 문서 갱신
```

## 검증 사항

- [ ] 화이트리스트 외 필드 정렬 시 400 응답
- [ ] size 상한 초과 시 400 응답
- [ ] 기본 page/size 값 동작 확인
- [ ] 다중 정렬 동작 확인 (`sort=A,asc&sort=B,desc`)
- [ ] 날짜 범위 검색 동작 확인 (precision별)
- [ ] Swagger UI에서 명세 명확성 확인
- [ ] common-lib 의존성 점검 (Spring Data 미포함 확인)

## 의사결정 기록

- **정렬 표현 방식**: `field,direction` 콤마 구분 채택 (`+/-` 접두사는 URL 인코딩 이슈로 미채택)
- **common-lib 정책**: 가벼움 유지, Spring Data 의존성 추가하지 않음 (순수 DTO만 위치)
- **변환 로직 위치**: 각 서비스의 static utility 클래스 (PageableConverter)
- **시간대**: UTC + ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss'Z'`) 일관 유지

## 비고

- 본 작업은 기존 동작 변경 (Breaking Change) → API 사용 측 영향 사전 공지 필요
- `PageableConverter`는 서비스마다 동일 코드가 중복될 수 있으나, common-lib 가벼움 유지 정책 우선
- 향후 변환 로직 변경 시 모든 서비스의 PageableConverter를 함께 갱신 필요
