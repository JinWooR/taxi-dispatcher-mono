# Springdoc OpenAPI Generic Response 정책

**상태**: 채택됨  
**이유**: CommonResponse<T> 제네릭 타입을 Swagger 문서에 정확히 표시하기 위해

## 정책

### 1. override-with-generic-response 설정

모든 application.yml에서:
```yaml
springdoc:
  override-with-generic-response: false
```

**이유**:
- `true` (기본값): 제네릭 타입 정리로 API 응답 스키마 단순화 시도
  - CommonResponse<T>의 T를 제거 → Object 또는 generic으로 표시
  - BaseGlobalExceptionHandler 제네릭 파라미터 처리 중 예외 발생 가능
- `false`: 원본 제네릭 타입 유지
  - CommonResponse<LoginResponse>, CommonResponse<RegisterResponse> 등 구체적 타입 표시
  - API 문서의 정확성 ↑

### 2. 적용 대상
- ✅ account-service (이미 적용)
- ✅ customer-service (향후)
- ✅ driver-service (향후)
- ✅ dispatcher-service (향후)

### 3. 확인 방법
```bash
# 로컬 개발 환경
curl http://localhost:8081/v3/api-docs

# Swagger UI
http://localhost:8081/swagger-ui.html
```

## 관련 문서
- troubleshooting/2026-05-19-swagger-generic-response-403.md
- docs/05-api-common-rules.md (Swagger UI 접근 경로)

## 참고
- [Springdoc OpenAPI Configuration](https://springdoc.org/#properties)
- CommonResponse<T> 구조: `shared/common-lib/response/CommonResponse.java`
