# 5️⃣ API 공통 규칙 정의

**상태**: 완료  
**마지막 업데이트**: 2026-05-16

---

## 📖 개요

택시 배차 MSA 시스템의 모든 API가 따를 공통 규칙을 정의합니다.
일관된 API 설계로 클라이언트 개발을 단순화하고 유지보수성을 높입니다.

---

## 📤 API 응답 포맷

### 성공 응답

```json
{
  "code": "SUCCESS",
  "message": "요청 성공",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2025-12-03T03:32:22Z"
}
```

**필드 설명**

| 필드 | 타입 | 설명 |
|------|------|------|
| `code` | String | 응답 상태 코드 (`SUCCESS`, 에러코드 등) |
| `message` | String | 사용자 친화적 메시지 |
| `data` | Object | 실제 응답 데이터 |
| `timestamp` | String | 응답 시간 (ISO 8601, UTC) |

### 페이징 응답

**요청**
```
GET /api/dispatches?page=0&size=20&sort=createdAt,desc
```

**쿼리 파라미터**

| 파라미터 | 타입 | 기본값 | 설명 |
|---------|------|-------|------|
| `page` | int | 0 | 페이지 번호 (0부터 시작) |
| `size` | int | 20 | 페이지 크기 |
| `sort` | String | - | 정렬 (필드명,asc/desc) |

**응답**

```json
{
  "code": "SUCCESS",
  "message": "조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "passengerId": 100,
        "driverId": 200,
        "status": "COMPLETED",
        "createdAt": "2025-12-03T03:32:22Z"
      },
      {
        "id": 2,
        "passengerId": 101,
        "driverId": 201,
        "status": "IN_PROGRESS",
        "createdAt": "2025-12-02T15:20:10Z"
      }
    ],
    "pageInfo": {
      "currentPage": 0,
      "pageSize": 20,
      "totalElements": 152,
      "totalPages": 8,
      "isFirst": true,
      "isLast": false,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2025-12-03T03:32:22Z"
}
```

**pageInfo 필드**

| 필드 | 타입 | 설명 |
|------|------|------|
| `currentPage` | int | 현재 페이지 (0부터 시작) |
| `pageSize` | int | 페이지 크기 |
| `totalElements` | long | 전체 요소 개수 |
| `totalPages` | int | 전체 페이지 수 |
| `isFirst` | boolean | 첫 페이지 여부 |
| `isLast` | boolean | 마지막 페이지 여부 |
| `hasNext` | boolean | 다음 페이지 존재 여부 |
| `hasPrevious` | boolean | 이전 페이지 존재 여부 |

---

## ❌ 에러 응답 포맷

```json
{
  "code": "ACCOUNT_NOT_FOUND",
  "message": "계정을 찾을 수 없습니다",
  "timestamp": "2025-12-03T03:32:22Z",
  "path": "/api/accounts/999"
}
```

**필드 설명**

| 필드 | 타입 | 설명 |
|------|------|------|
| `code` | String | 에러 코드 (상세 내용 참고) |
| `message` | String | 에러 메시지 |
| `timestamp` | String | 에러 발생 시간 (ISO 8601, UTC) |
| `path` | String | 요청 경로 |

**HTTP 상태 코드와 함께 반환됨**
```
200 OK → code: SUCCESS
400 Bad Request → code: INVALID_REQUEST
401 Unauthorized → code: UNAUTHORIZED
404 Not Found → code: ACCOUNT_NOT_FOUND
500 Internal Server Error → code: INTERNAL_SERVER_ERROR
```

---

## 🚨 에러 코드 정의

### 공통 에러 코드

| 코드 | HTTP 상태 | 설명 |
|------|----------|------|
| `SUCCESS` | 200 | 요청 성공 |
| `INVALID_REQUEST` | 400 | 잘못된 요청 |
| `UNAUTHORIZED` | 401 | 인증 실패 |
| `FORBIDDEN` | 403 | 접근 권한 없음 |
| `NOT_FOUND` | 404 | 리소스 없음 |
| `DUPLICATE` | 409 | 중복된 데이터 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 |

### 서비스별 에러 코드

**Account Service**
- `ACCOUNT_NOT_FOUND` - 계정을 찾을 수 없음
- `ACCOUNT_DUPLICATE_EMAIL` - 이미 사용 중인 이메일
- `ACCOUNT_INVALID_PASSWORD` - 비밀번호가 올바르지 않음
- `ACCOUNT_INACTIVE` - 비활성화된 계정

**Customer Service**
- `CUSTOMER_NOT_FOUND` - 사용자를 찾을 수 없음
- `CUSTOMER_PROFILE_INCOMPLETE` - 프로필이 불완전함
- `CUSTOMER_INVALID_PHONE` - 유효하지 않은 전화번호

**Driver Service**
- `DRIVER_NOT_FOUND` - 기사를 찾을 수 없음
- `DRIVER_INVALID_LICENSE` - 유효하지 않은 면허
- `DRIVER_OFFLINE` - 기사가 오프라인 상태

**Dispatcher Service**
- `DISPATCH_NOT_FOUND` - 배차를 찾을 수 없음
- `DISPATCH_NO_AVAILABLE_DRIVER` - 이용 가능한 기사가 없음
- `DISPATCH_ALREADY_ASSIGNED` - 이미 배차된 상태

---

## 🔐 인증/인가 방식: JWT

### JWT 토큰 발급

**엔드포인트**: `POST /api/accounts/login`

```json
// 요청
{
  "email": "john@example.com",
  "password": "password123"
}

// 응답 (200 OK)
{
  "code": "SUCCESS",
  "message": "로그인 성공",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2025-12-03T03:32:22Z"
}
```

### JWT 토큰 사용

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 토큰 설정

```yaml
# application.yml
jwt:
  secret: your-secret-key-here-change-in-production
  expiration: 3600  # 1시간 (초 단위)
  refresh-expiration: 604800  # 7일
```

### 로그아웃

토큰 기반 인증이므로:
- 클라이언트에서 토큰 삭제
- 서버 측 토큰 블랙리스트 (선택)

---

## ✅ 유효성 검사

Spring Validation 기반 어노테이션 사용

```java
public class AccountRequest {
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 50, message = "비밀번호는 8~50자여야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;
}
```

**컨트롤러에서 검증**

```java
@PostMapping("/accounts")
public ApiResponse<AccountResponse> create(
    @Valid @RequestBody AccountRequest request
) {
    // request는 자동으로 검증됨
    // 검증 실패 시 400 Bad Request + 에러 메시지
}
```

**검증 실패 응답**

```json
{
  "code": "INVALID_REQUEST",
  "message": "입력 값 검증 실패",
  "timestamp": "2025-12-03T03:32:22Z",
  "errors": [
    {
      "field": "email",
      "message": "유효한 이메일 형식이 아닙니다"
    },
    {
      "field": "password",
      "message": "비밀번호는 8~50자여야 합니다"
    }
  ]
}
```

---

## 📚 API 문서: Springdoc OpenAPI (인터페이스 기반)

### API 인터페이스 정의

```java
@RequestMapping("/api/accounts")
public interface AccountApi {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "계정 조회",
        description = "ID로 계정 정보를 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "계정을 찾을 수 없음")
    })
    ApiResponse<AccountResponse> getAccount(
        @Parameter(description = "계정 ID")
        @PathVariable Long id
    );
    
    @PostMapping
    @Operation(
        summary = "계정 생성",
        description = "새로운 계정을 생성합니다"
    )
    ApiResponse<AccountResponse> create(
        @RequestBody AccountRequest request
    );
}
```

### 컨트롤러 구현 (깔끔함)

```java
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    
    private final AccountService accountService;
    
    @Override
    public ApiResponse<AccountResponse> getAccount(Long id) {
        Account account = accountService.getAccount(id);
        return ApiResponse.success(AccountResponse.from(account));
    }
    
    @Override
    public ApiResponse<AccountResponse> create(AccountRequest request) {
        Account account = accountService.create(request);
        return ApiResponse.success(AccountResponse.from(account));
    }
}
```

### Swagger UI 접근

**현재 (Phase 1): 서비스별 개별 접근**
- Account Service: http://localhost:8081/swagger-ui.html
- Customer Service: http://localhost:8082/swagger-ui.html
- Driver Service: http://localhost:8083/swagger-ui.html
- Dispatcher Service: http://localhost:8084/swagger-ui.html

**추후 고려사항 (Phase 2)**
- API Gateway 도입 시 단일 진입점에서 통합 Swagger UI 제공 가능
- Spring Cloud Gateway + Springdoc OpenAPI 조합으로 구현
- 모든 API를 한곳에서 확인 가능

---

## 🕐 타임스탐프 정책

### 저장: Unix Timestamp

```java
@Entity
public class Dispatch {
    @Id
    private Long id;
    
    @CreationTimestamp
    @Column(columnDefinition = "BIGINT")
    private Long createdAt;  // Unix Timestamp (초 단위)
    
    @UpdateTimestamp
    @Column(columnDefinition = "BIGINT")
    private Long updatedAt;  // Unix Timestamp
}
```

### 응답: ISO 8601 with Z

```json
{
  "code": "SUCCESS",
  "data": {
    "id": 1,
    "createdAt": "2025-12-03T03:32:22Z",
    "updatedAt": "2025-12-03T03:32:22Z"
  },
  "timestamp": "2025-12-03T03:32:22Z"
}
```

### 설정

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
  jackson:
    serialization:
      write-dates-as-timestamps: false
    time-zone: UTC
```

### 클라이언트 변환

```javascript
// 서버 응답: ISO 8601 (UTC, Z)
const createdAt = "2025-12-03T03:32:22Z";

// JavaScript: 자동으로 로컬 타임존으로 변환
const date = new Date(createdAt);
console.log(date.toLocaleString('ko-KR'));
// → "2025. 12. 3. 오후 12:32:22" (한국 기준)

// React에서
<span>{new Date(createdAt).toLocaleDateString('ko-KR')}</span>
```

### 날짜 범위 검색

```javascript
// 클라이언트: 로컬 시간으로 "2025-12-01 ~ 2025-12-31" 요청
const startDate = new Date("2025-12-01");
const endDate = new Date("2025-12-31");

// UTC로 변환 후 요청
POST /api/dispatches/search
{
  "startDate": startDate.toISOString(),  // "2025-12-01T00:00:00.000Z"
  "endDate": endDate.toISOString()      // "2025-12-31T23:59:59.999Z"
}

// 서버: 단순 숫자 범위 검색
WHERE created_at >= 1733011200 AND created_at <= 1735689599
```

---

## 🌐 CORS (Cross-Origin Resource Sharing)

### 개발 환경 (application-dev.yml)

```yaml
cors:
  allowed-origins: "*"
  allowed-methods: 
    - GET
    - POST
    - PUT
    - DELETE
  allowed-headers: "*"
  allow-credentials: false
  max-age: 3600
```

### 운영 환경 (application-prod.yml)

```yaml
cors:
  allowed-origins:
    - "https://example.com"
    - "https://app.example.com"
    - "https://admin.example.com"
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
  allowed-headers:
    - "Authorization"
    - "Content-Type"
  allow-credentials: true
  max-age: 86400
```

### Spring 설정 클래스

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## 📋 API 버저닝

**현재**: 버저닝 없음 (v1 생략)

```
GET /api/accounts
GET /api/customers
GET /api/drivers
```

**향후 필요 시**: URL 경로에 버전 추가

```
GET /api/v1/accounts
GET /api/v2/accounts  (변경사항 있을 경우)
```

---

## ⏱️ Rate Limiting

**현재**: 구현 없음

**향후 추가 예정**:
- 분당 100 요청 제한
- IP별 또는 사용자별 제한

---

## 📝 구현 예시

### 공통 응답 클래스

```java
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            "SUCCESS",
            "요청 성공",
            data,
            LocalDateTime.now(ZoneId.of("UTC"))
        );
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(
            code,
            message,
            null,
            LocalDateTime.now(ZoneId.of("UTC"))
        );
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
        MethodArgumentNotValidException e
    ) {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("INVALID_REQUEST", "입력 값 검증 실패"));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFound(
        EntityNotFoundException e
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", e.getMessage()));
    }
}
```

---

## ✅ 확인 사항

- [ ] 응답 포맷 가이드 문서 공유
- [ ] 에러 코드 표 완성
- [ ] JWT 설정 (secret key) 확정
- [ ] API 인터페이스 정의 방식 팀원 공유
- [ ] Springdoc OpenAPI 설정 완료
- [ ] CORS 환경별 설정 확인
- [ ] 타임스탬프 변환 유틸리티 작성

---

**승인 날짜**: 2026-05-16
