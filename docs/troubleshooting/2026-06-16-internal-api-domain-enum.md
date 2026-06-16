# 내부 API 응답에 도메인 enum 노출 정책

## 상황
- driver-service 내부 API 응답 DTO를 common-lib로 분리
- 응답에 도메인 enum 필드(`WorkSessionStatus`) 포함 필요
- 호출 서비스(tracking-service 등)는 driver-service 도메인 의존성 없이 응답 파싱 필요

## 검토 옵션

### A. 도메인 enum / VO를 common-lib로 이동
- 모든 서비스가 동일 enum 공유
- 타입 안정성 ↑

### B. 응답 DTO에서 enum 필드를 String으로 변환
- common-lib에 도메인 침투 없음
- 호출 서비스는 String 그대로 사용 (필요 시 자체 enum 매핑)

## 결정: B (String 변환)

## 이유
- common-lib에 도메인이 침투하면 마이크로서비스 경계가 흐려짐
- 한 서비스의 enum 변경(값 추가/삭제)이 다른 서비스의 라이브러리 버전 강제 업데이트 유발 → 결합도↑
- 전이 규칙 같은 도메인 행위가 enum에 들어갈 경우 common-lib에 비즈니스 로직이 들어감 → 레이어 위반

## 적용
- common-lib DTO의 enum 필드는 `String`
- 도메인 → DTO 변환은 호출 서비스의 application 레이어에서 `enum.name()`으로 수행
- 예: `WorkSessionService.toInternalProfile()`이 `workSession.getStatus().name()` 직렬화

## 체크리스트
- [x] `DriverInternalWorkSession.status` String 타입 적용
- [x] `WorkSessionService.toInternalProfile()`에서 `.name()` 변환
