# Swagger API Docs 403 에러 (GlobalExceptionHandler 추가 후)

## 증상
```
POST /v3/api-docs → 403 Forbidden
Swagger UI 페이지 로드 불가능
```

발생 시점: GlobalExceptionHandler 추가 및 @RestControllerAdvice 적용 후

## 원인
Springdoc OpenAPI의 `override-with-generic-response` 설정이 **기본값 true**로 설정되어 있음.

이 설정이 활성화되면:
- CommonResponse<T>처럼 제네릭을 포함한 API 응답이 스캔될 때
- Springdoc이 제네릭 타입을 정리(override)하려고 시도
- BaseGlobalExceptionHandler의 제네릭 파라미터 `<?>` 처리 중 충돌 발생
- 결과: 스키마 생성 중 예외 → 403 응답

## 해결

### application.yml 수정
```yaml
springdoc:
  override-with-generic-response: false  # 추가
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
  api-docs:
    path: /v3/api-docs
```

## 체크리스트
- [x] 원인 파악: override-with-generic-response 설정
- [x] 해결 방법 적용: application.yml에 false 설정
- [x] Swagger UI 재로드 성공
- [x] /v3/api-docs 정상 응답 확인
- [x] BaseGlobalExceptionHandler와 공통 응답 제네릭 타입 정상 표시됨

## 학습 포인트
- Springdoc의 제네릭 처리 설정에 주의
- CommonResponse<T>같은 제네릭 래퍼는 override-with-generic-response=false 권장
- API 문서 생성 중 예외는 개발 단계에서 HTTP 403으로 표시될 수 있음

## 최종 결정
→ `decisions/2026-05-19-springdoc-generic-response-policy.md`에서 공식 정책 등록
