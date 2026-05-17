# 🔐 Common Library

공유 라이브러리 - 모든 마이크로서비스가 사용하는 공용 기능

JWT 기반 인증, 보안 설정, 공통 상수 등을 제공합니다.

---

## 📚 포함 내용

### JWT Authentication

#### JwtTokenProvider
토큰 생성 및 검증을 담당합니다.

```java
// 토큰 생성
String token = jwtTokenProvider.generateToken(
    accountId,      // Long
    type,           // "USER" or "DRIVER"
    email           // String
);

// 토큰 검증 및 사용자 정보 추출
AuthUser authUser = jwtTokenProvider.validateAndGetUser(token);
Long accountId = authUser.getAccountId();
String type = authUser.getType();
```

#### JwtAuthenticationFilter
`Authorization` 헤더에서 토큰을 추출하고 Spring Security Context에 설정합니다.

#### AuthUser
인증된 사용자 정보를 담는 객체입니다.

```java
@Data
public class AuthUser {
    private Long accountId;   // Account ID
    private String type;      // USER | DRIVER
    private String email;     // 이메일
}
```

---

## 🔌 의존성

각 마이크로서비스에서:

```gradle
dependencies {
    implementation project(':shared:common-lib')
}
```

---

## ⚙️ 설정 (application.yml)

```yaml
jwt:
  secret: "your-secret-key-min-256-bits-for-hs512"
  expiration: 3600000              # 1시간 (밀리초)
  refresh-expiration: 86400000     # 24시간 (밀리초)
  issuer: "taxi-dispatcher"
  audience: "taxi-dispatcher-users"
```

---

## 💡 사용 방법

### 1️⃣ 토큰 생성 (로그인 시)

```java
@PostMapping("/login")
public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
    // ... 사용자 인증 로직
    
    String token = jwtTokenProvider.generateToken(
        account.getId(),
        account.getType(),  // "USER" or "DRIVER"
        account.getEmail()
    );
    
    return ApiResponse.success(new LoginResponseDto(token));
}
```

### 2️⃣ 토큰 검증 (API 호출 시)

Spring Security가 자동으로 처리합니다.

```java
@GetMapping("/profile")
public ApiResponse<UserProfileDto> getProfile(
    @AuthenticationPrincipal AuthUser authUser
) {
    // authUser는 토큰에서 추출한 정보
    Long accountId = authUser.getAccountId();
    String type = authUser.getType();
    
    // ... 로직
}
```

---

## 🔒 보안 특징

- **토큰 정보**: accountId, type(USER|DRIVER), email만 포함
- **DB 조회 불필요**: 토큰 검증만으로 인증 완료
- **서명 알고리즘**: HS512 (HMAC with SHA-512)
- **만료 시간**: 기본 1시간 (설정 가능)

---

## 📝 Constants

`SecurityConstant` 클래스에서 보안 관련 상수를 정의합니다.

```java
public static final String USER_TYPE_USER = "USER";
public static final String USER_TYPE_DRIVER = "DRIVER";
public static final String TOKEN_TYPE_BEARER = "Bearer";
```

---

**생성일**: 2026-05-17  
**상태**: Active  
**버전**: 1.0.0
