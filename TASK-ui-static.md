# 정적 UI 호스팅 작업 계획서 (API 흐름 검증용)

## 배경

개발된 API의 흐름(인증 → 프로필 등록 → 배차 요청 → 승인 등)을 시각적으로 확인할 수단이 부재. Swagger UI는 단위 호출에 적합하지만 다단계 시나리오 검증에는 비효율적.

## 목적

- 개발한 API의 비즈니스 흐름을 화면 단위로 시연/검증
- 시연 시 외부 환경 의존성 최소화 (`docker-compose up` 한 번이면 동작)
- 본 프로젝트는 학습/검증용이므로 **프론트 구현은 최소한**으로 유지

## 정책

- **단순 정적 HTML/CSS/JS (vanilla)** 만 사용
- 빌드 환경/패키지 매니저 (npm, vite 등) **사용 안 함**
- 프레임워크 (React, Vue 등) **사용 안 함**
- 호스팅은 **Docker nginx alpine** 컨테이너 (기존 `docker-compose.yml` 에 추가)
- 포트: **3000** 외부 노출
- 정적 파일은 호스트 디렉토리 `./ui` 를 volume mount → 수정 즉시 반영

## 인프라 구성

### docker-compose 추가

```yaml
ui:
  image: nginx:alpine
  container_name: ui
  ports:
    - "3000:80"
  volumes:
    - ./ui:/usr/share/nginx/html:ro
  networks:
    - taxi-network
```

### CORS 처리

- UI origin: `http://localhost:3000`
- 각 서비스의 dev 환경 CORS 설정에 해당 origin 허용 필요
  - 현재 `cors.allowed-origins: "*"` 이면 우선 동작
  - 운영성 강화 시 `http://localhost:3000` 명시 추가 검토

### 접근 URL

- `http://localhost:3000/` → `index.html`
- `http://localhost:3000/pages/login.html` 등

## 디렉토리 구조

```
project-root/
  ui/
    index.html              # 진입 (역할 선택 또는 대시보드)
    pages/
      login.html            # 로그인
      register.html         # 회원가입 (account)
      customer.html         # 고객 (프로필/배차 요청/내 배차)
      driver.html           # 기사 (프로필/pending 배차/승인-거절/위치)
      dispatch-detail.html  # 배차 상세 (선택)
    common/
      api.js                # fetch 래퍼 + 응답 파싱
      auth.js               # 로그인 상태 + 토큰 관리 (localStorage)
      config.js             # 서비스별 baseURL 정의
    styles/
      main.css
  docker-compose.yml        # ui 컨테이너 추가
```

## 화면 구성

| 화면 | 주요 흐름 | 호출 API |
|------|----------|----------|
| 로그인 | 로그인 → JWT 저장 | `POST /api/auth/login` |
| 회원가입 | 계정 생성 → 자동 로그인 (선택) | `POST /api/auth/register` |
| 고객 - 프로필 | 프로필 등록/조회/수정 | `POST/GET/PUT /api/customers/me` |
| 고객 - 배차 요청 | 출발지/도착지 입력 → 배차 생성 | `POST /api/dispatches/customers` |
| 고객 - 내 배차 | 페이징 목록 (정렬: requestedAt) | `GET /api/dispatches/customers` |
| 기사 - 프로필 | 프로필 등록/조회/수정/위치 갱신 | `POST/GET/PUT /api/drivers/me` |
| 기사 - Pending 목록 | 페이징 (정렬: requestedAt) | `GET /api/dispatches/drivers/pending` |
| 기사 - 승인/거절 | 단일 배차 승인/거절 | `POST /api/dispatches/drivers/{id}/accept` `/reject` |
| 배차 상세 | 상태/타임스탬프 확인 | `GET /api/dispatches/{id}` |

## 작업 범위 & 체크리스트

### Phase 1: 인프라

- [x] `docker-compose.yml` 에 `ui` 컨테이너 추가 (port 3000, volume mount) ✅ 완료
- [x] 빈 `ui/index.html` placeholder 작성 → 컨테이너 정상 동작 확인 ✅ 완료
- [ ] (필요 시) 서비스별 dev CORS 설정에 `http://localhost:3000` 명시

### Phase 2: 공통 모듈

- [x] `ui/common/config.js` (각 서비스 baseURL 상수) ✅ 완료
- [x] `ui/common/api.js` (fetch 래퍼, CommonResponse 파싱, 에러 처리) ✅ 완료 + 토큰 재발급
- [x] `ui/common/auth.js` (토큰 저장/조회/삭제, 로그인 상태 확인) ✅ 완료 + refreshToken 관리
- [x] `ui/styles/main.css` (최소 스타일) ✅ 완료
- [x] `ui/common/services/` (서비스 계층: AuthService, CustomerService, DriverService) ✅ 완료
- [x] `ui/common/types/` (타입 계층: Auth, Customer, Driver) ✅ 완료

### Phase 3: 인증 화면

- [x] `pages/auth/login.html` (로그인 폼 + JWT 저장) ✅ 완료
- [x] `pages/auth/register.html` (회원가입 폼) ✅ 완료
- [x] 토큰 만료/401 처리 (자동 로그인 화면 이동) ✅ 완료 (ApiClient에서 자동 처리)

### Phase 4: 고객 화면

- [ ] `pages/customer.html` 내 영역 분할
  - [ ] 프로필 등록/조회/수정
  - [ ] 배차 요청 (출발지/도착지 입력)
  - [ ] 내 배차 목록 (페이징/정렬)
  - [ ] 배차 상세 진입

### Phase 5: 기사 화면

- [ ] `pages/driver.html` 내 영역 분할
  - [ ] 프로필 등록/조회/수정
  - [ ] 위치 업데이트
  - [ ] Pending 배차 목록 (페이징/정렬)
  - [ ] 배차 승인/거절
  - [ ] 배차 상세 진입

### Phase 6: 진입 화면

- [ ] `index.html` 에서 로그인 상태에 따라 적절한 화면 라우팅 (CUSTOMER/DRIVER)
- [ ] 미로그인 시 `pages/login.html` 이동

## 검증 사항

- [ ] `docker-compose up` 시 `http://localhost:3000` 접근 확인
- [ ] 호스트의 `ui/` 변경 시 컨테이너 재기동 없이 반영 확인 (volume mount)
- [ ] 로그인 → JWT 발급 → 다음 화면에서 인증 헤더 자동 첨부 확인
- [ ] 고객 시나리오: 회원가입 → 프로필 등록 → 배차 요청 → 내 배차 목록에서 확인
- [ ] 기사 시나리오: 회원가입 → 프로필 등록 → 위치 갱신 → Pending 조회 → 승인
- [ ] 배차 흐름 시나리오: 고객 요청 → 기사 승인 → 상세에서 ASSIGNED 확인
- [ ] CORS 차단 발생 없이 모든 API 호출 동작 확인
- [ ] 토큰 만료/401 시 로그인 화면 이동 확인

## 의사결정 기록

- **프레임워크 미사용**: vanilla HTML/CSS/JS 만 사용 (학습 부담 / 빌드 환경 제거)
- **호스팅 방식**: Docker nginx alpine 컨테이너
  - 로컬 nginx 대비 환경 의존성 제거 + 다른 사용자 재현 용이
  - `file://` 직접 실행 대비 CORS/모듈 로딩 이슈 회피
- **포트**: 3000 (개발 시 통상 프론트 포트로 익숙)
- **파일 마운트**: 컨테이너 재빌드 없이 수정 즉시 반영 (volume mount)
- **상태 관리**: localStorage 기반 JWT 저장 (단순 검증 목적이므로 무방)
- **서비스 아키텍처**: 비즈니스 로직을 서비스 객체로 추상화
  - Pages → Services (with Type Classes) → ApiClient → Auth + Config
- **타입 안전성**: TypeScript 없이 클래스 기반 타입 관리
  - JSDoc으로 IDE 타입 힌트 제공
  - 요청/응답 검증 및 변환 자동화
- **토큰 재발급**: 401 에러 시 자동으로 refreshToken 사용해서 새 토큰 발급
  - 재발급 실패 시 자동 로그아웃

## 진행 상황

**✅ 완료 (2026-06-05)**

### Phase 1, 2 & 3 완료 (기본 인프라 + 공통 모듈 + 인증 화면)

**인프라:**
- docker-compose.yml: nginx ui 컨테이너 추가
- ui/index.html: 진입점 (인증 상태 확인 후 대시보드 라우팅)
- ui/styles/main.css: 기본 스타일

**공통 모듈:**
- ui/common/config.js: API URL 설정
- ui/common/api.js: fetch 래퍼 + 토큰 재발급 자동 처리
- ui/common/auth.js: JWT 토큰 및 refreshToken 관리

**서비스 계층:**
- ui/common/services/AuthService.js: 로그인, 회원가입, 토큰 재발급
- ui/common/services/CustomerService.js: 고객 프로필, 배차 관리
- ui/common/services/DriverService.js: 기사 프로필, 배차 승인/거절

**타입 계층:**
- ui/common/types/Auth.js: 인증 요청/응답 타입 + 검증
- ui/common/types/Customer.js: 고객 프로필/배차 타입 + 비즈니스 로직
- ui/common/types/Driver.js: 기사 프로필/배차 타입 + 비즈니스 로직

**샘플 코드:**
- ui/samples/: 완전한 구현 예제 (로그인, 고객/기사 대시보드)
- ui/samples/services/: 서비스 참고용
- ui/samples/types/: 타입 클래스 참고용

**문서:**
- docs/06-ui-frontend.md: 개발 가이드 (기술 정책, 구조, 스타일, 서비스, 타입)

**인증 화면 (Phase 3):**
- ui/pages/auth/login.html: 로그인 폼 (이메일, 비밀번호, 제출 버튼)
- ui/pages/auth/login.js: 로그인 로직 (AuthService.login() 호출, 토큰 저장, 역할별 리다이렉트)
- ui/pages/auth/register.html: 회원가입 폼 (이메일, 비밀번호, 역할 선택)
- ui/pages/auth/register.js: 회원가입 로직 (AuthService.register() 호출, 로그인 페이지로 이동)

### ⏳ 예정 (Phase 4-6)

- Phase 4: 고객 화면 (프로필, 배차 요청/관리)
- Phase 5: 기사 화면 (프로필, pending 배차, 승인/거절)
- Phase 6: 진입점 라우팅

## 비고

- 본 UI는 **API 흐름 검증 도구**일 뿐, 운영용/사용자용 프론트가 아님
- 디자인/UX 품질은 최소 수준 유지 (학습 본질에 집중)
- 신규 API 추가 시 해당 화면/공통 모듈에 흐름 반영 필요
- 향후 본격 프론트 도입 시 본 UI 는 제거 또는 별도 프로젝트로 분리
