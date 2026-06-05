# Swagger 명세 표준화 작업 계획서

## 배경

현재 프로젝트의 Swagger 명세는 다음 문제가 존재.

- Request/Response DTO 필드에 `@Schema` 설명이 없어 Swagger UI에서 의미 파악이 어려움
- 객체 형태의 Query String 입력 시 `@ParameterObject` 누락으로 명세가 부정확
- 페이지네이션 `sort` 옵션이 Swagger UI에 명확히 노출되지 않음
- 서비스별로 Swagger 적용 수준이 다름 (일관성 부족)

## 목표

- 전 서비스에 걸친 Swagger 명세 일관성 확보
- Swagger UI 사용자(클라이언트 개발자)의 입력/응답 이해도 향상
- 외부 도구 연동 시 OpenAPI 명세 신뢰성 향상

## 정책 (전 서비스 공통)

### 1. Request/Response DTO

- **모든 DTO 필드에 `@Schema(description = "...")` 명시**
- 필요 시 `example`, `defaultValue` 도 함께 제공
- 예외: 너무 단순하거나 의미가 자명한 필드는 생략 가능 (아래 페이지네이션 항목 참조)

### 2. Query String 객체 입력

- API 인터페이스에서 **객체를 Query String으로 받는 경우 `@org.springdoc.core.annotations.ParameterObject` 선언 필수**
- 위치: **API 인터페이스 (Api.java)** 만 적용 (Controller 구현체에는 불필요, 인터페이스 메타데이터가 상속됨)
- 예시:
  ```java
  ResponseEntity<...> getMyDispatches(
      @AuthenticationPrincipal AuthUser authUser,
      @ParameterObject @Valid CustomerDispatchPageRequest pageRequest);
  ```

### 3. 공용 페이지네이션 (PageableRequest)

- `page`, `size`: 의미가 자명하므로 `@Schema` 설명 누락 허용
- `sort`: **반드시 `@Schema` 로 Swagger 항목 노출 필요**
  - `field,direction` 형식 안내
  - API별 허용 정렬 필드는 자식 클래스에서 명시 (자식 클래스 description 활용)

## 작업 범위

### Phase 1: 공용 라이브러리 (`shared/common-lib`)

- [x] `PageableRequest.sort` 필드에 `@Schema` 적용 (page/size는 생략 가능)
- [x] `DateRangeRequest` 필드에 `@Schema` 적용
- [x] 내부 통신 공용 DTO (`CustomerInternalProfile`, `DriverInternalProfile`) 필드에 `@Schema` 적용

### Phase 2: 각 서비스 Api 인터페이스 적용

서비스별 공통 작업:
- API 인터페이스의 객체형 Query String 파라미터에 `@ParameterObject` 추가
- Operation 설명에 페이지네이션/정렬 옵션 안내 보강 (해당 API만)

서비스별 진행 상태:
- [x] **account-service** (`AuthApi`) — 객체형 Query String 없음, 변경 없음
- [x] **customer-service** (`CustomerApi`, `InternalCustomerApi`) — 객체형 Query String 없음, 변경 없음
- [x] **dispatcher-service** (`DispatchApi`) — `getMyDispatches`, `getPendingDispatches`에 `@ParameterObject` 적용
- [x] **driver-service** (`DriverApi`, `InternalDriverApi`) — `@RequestParam` 단일 사용, 변경 없음

### Phase 3: 각 서비스 DTO 적용

서비스별 공통 작업:
- Request/Response DTO 필드에 `@Schema(description=...)` 적용
- 필수 입력 필드는 `@Schema(requiredMode = Schema.RequiredMode.REQUIRED)` 사용
- 예시 값(`example`) 적극 활용 (이메일, UUID, 좌표 등)
- 페이지네이션 자식 클래스(예: `CustomerDispatchPageRequest`)는 **클래스 레벨 `@Schema(description)` 로 허용 정렬 필드 명시**

서비스별 진행 상태:
- [x] **account-service**
  - Request: `LoginRequest`, `RegisterRequest`
  - Response: `LoginResponse`, `RegisterResponse`, `TokenInfo`
- [ ] **customer-service**
  - Request: `RegisterCustomerRequest`, `UpdateCustomerRequest`
  - Response: `CustomerProfileResponse`
- [ ] **dispatcher-service**
  - Request: `CreateDispatchRequest`, `UpdateDispatchStatusRequest`
  - Request (페이지): `CustomerDispatchPageRequest`, `DriverPendingPageRequest`
  - Response: `DispatchResponse`
- [ ] **driver-service**
  - Request: `RegisterDriverRequest`, `UpdateDriverRequest`, `ChangeStatusRequest`, `UpdateLocationRequest`
  - Response: `DriverResponse`

### Phase 4: 공통 문서 갱신 (`docs/05-api-common-rules.md`)

- [ ] Swagger 명세 표준 추가
  - DTO 필드 `@Schema` 적용 정책
  - Query String 객체 입력 시 `@ParameterObject` 적용 정책
  - 페이지네이션 sort 항목 노출 정책

## 마이그레이션 순서

```
1. common-lib (공용 DTO @Schema 적용)
   ↓
2. dispatcher-service (페이지네이션 적용 사례 검증)
   ↓
3. driver / customer / account-service 순차 적용
   ↓
4. 공통 문서 갱신
```

## 검증 사항

- [ ] Swagger UI에서 모든 DTO 필드의 description 확인
- [ ] Query String 객체 파라미터가 개별 필드로 명세화 확인 (`@ParameterObject` 효과)
- [ ] `sort` 파라미터의 `field,direction` 안내 표시 확인
- [ ] 페이지네이션 자식 클래스에서 허용 정렬 필드 안내 노출 확인
- [ ] OpenAPI JSON (`/v3/api-docs`) 출력 확인 (외부 도구 연동 가능 수준)

## 의사결정 기록

- **`@Schema` 적용 범위**: 모든 DTO 필드 (단순한 페이지네이션 page/size는 생략 허용)
- **`@ParameterObject` 적용 위치**: API 인터페이스만 (Controller 구현체에는 불필요)
- **페이지네이션 정책**:
  - `page`, `size`: 설명 누락 OK (의미 자명)
  - `sort`: Swagger 항목 제공 필수 (입력 방법 안내)
- **허용 정렬 필드 안내 위치**: 자식 클래스 레벨 `@Schema(description=...)`

## 비고

- 본 작업은 동작 변경 없음 (Swagger 명세 메타데이터만 강화) → Breaking Change 아님
- SpringDoc 의존성은 각 서비스에 이미 포함됨 (common-lib에는 `compileOnly` 로 존재)
- 향후 신규 DTO/Api 추가 시 본 표준을 적용하도록 `docs/05-api-common-rules.md` 에 명시
