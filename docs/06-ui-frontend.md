# 6️⃣ 프론트엔드 개발 가이드 (정적 UI)

**상태**: 진행 중  
**마지막 업데이트**: 2026-06-05

---

## 📋 개요

본 가이드는 **API 흐름 검증용 정적 UI**의 개발 방식을 정의합니다.
- **목적**: 개발된 API의 비즈니스 흐름을 화면 단위로 시연/검증
- **특성**: 최소한의 프론트엔드 구현 (학습/검증용)
- **기술**: Vanilla HTML/CSS/JS 만 사용

---

## 🛠️ 기술 정책

### 사용 가능
- ✅ HTML5, CSS3, JavaScript (ES6+)
- ✅ `fetch` API
- ✅ `localStorage` (토큰 저장)
- ✅ Docker nginx (배포)

### 사용 불가능
- ❌ npm / 패키지 매니저 (yarn, pnpm 등)
- ❌ 빌드 도구 (webpack, vite, rollup 등)
- ❌ 프레임워크 (React, Vue, Angular 등)
- ❌ 트랜스파일러 (Babel, TypeScript 등)
- ❌ 린터/포매터 (ESLint, Prettier 등)

**이유**: 단순성 유지 + 학습 환경에서의 환경 의존성 제거

---

## 🏗️ 개발 환경

### 실행 방식
```bash
# 터미널에서 (프로젝트 루트)
docker-compose up

# 브라우저에서 접속
http://localhost:3000
```

### 파일 수정 및 반영
- 호스트의 `ui/` 디렉토리 파일 수정 시 **컨테이너 재빌드 불필요**
- Volume mount로 즉시 반영 (새로고침만 필요)

### 개발 도구
- 코드 편집기 (VSCode, IntelliJ 등)
- 브라우저 개발자 도구 (DevTools)

---

## 📁 디렉토리 구조 및 역할

```
project-root/
├── ui/                          # 정적 UI 호스팅 루트
│   ├── index.html              # 진입점 (로그인 상태 확인 → 대시보드 라우팅)
│   ├── pages/
│   │   ├── auth/               # 인증 (모든 사용자)
│   │   │   ├── login.html
│   │   │   └── register.html
│   │   ├── customer/           # 고객 권한 화면
│   │   │   ├── index.html      # 고객 대시보드
│   │   │   ├── profile.html    # 프로필 관리
│   │   │   └── dispatch/
│   │   │       ├── request.html  # 배차 요청
│   │   │       ├── list.html     # 내 배차 목록
│   │   │       └── detail.html   # 배차 상세
│   │   └── driver/             # 기사 권한 화면
│   │       ├── index.html      # 기사 대시보드
│   │       ├── profile.html    # 프로필 관리
│   │       └── dispatch/
│   │           ├── pending.html  # Pending 배차 목록
│   │           ├── accept.html   # 승인/거절
│   │           └── detail.html   # 배차 상세
│   ├── common/                  # 공통 모듈 (재사용 가능)
│   │   ├── api.js              # fetch 래퍼, CommonResponse 파싱
│   │   ├── auth.js             # JWT 저장/조회/검증
│   │   └── config.js           # 서비스별 baseURL 정의
│   ├── styles/                  # 스타일시트
│   │   └── main.css            # 공통 스타일
│   └── samples/                 # 개발 참고용 샘플 파일
│       ├── index.html      # 진입점 샘플
│       ├── config.js
│       ├── api.js
│       ├── auth.js
│       └── pages/
│           ├── auth/
│           │   ├── login.html
│           │   └── login.js
│           ├── customer/
│           │   ├── index.html
│           │   └── index.js
│           └── driver/
│               ├── index.html
│               └── index.js
└── docker-compose.yml           # ui 컨테이너 설정
```

### 각 디렉토리 역할

| 디렉토리 | 역할 | 설명 |
|---------|------|------|
| `ui/` | UI 호스팅 루트 | nginx가 서빙하는 정적 파일 루트 |
| `pages/` | 화면 구현 | 권한별로 구분된 사용자 화면 HTML |
| `pages/auth/` | 인증 화면 | 로그인, 회원가입 (모든 사용자) |
| `pages/customer/` | 고객 화면 | 프로필, 배차 요청/관리 |
| `pages/driver/` | 기사 화면 | 프로필, Pending 배차 관리 |
| `common/` | 공통 로직 | 모든 화면에서 사용하는 재사용 모듈 |
| `styles/` | 공통 스타일 | 글로벌 CSS (리셋, 레이아웃, 공통 컴포넌트) |
| `samples/` | 개발 참고용 | API 래핑, 인증 관리 등의 구현 예제 |

---

## 📐 프론트엔드 스타일 가이드

### 1️⃣ 네이밍 컨벤션

**JavaScript**
```javascript
// ✅ Good: camelCase
function fetchUserProfile() { }
const userToken = localStorage.getItem('token');
let isLoading = false;

// ❌ Bad: snake_case, PascalCase
function fetch_user_profile() { }
const UserToken = localStorage.getItem('token');
```

**HTML / CSS**
```html
<!-- ✅ Good: kebab-case -->
<div id="login-form"></div>
<button class="btn-primary">로그인</button>

<!-- ❌ Bad: camelCase, snake_case -->
<div id="loginForm"></div>
<button class="btn_primary">로그인</button>
```

### 2️⃣ 함수 및 모듈 구조

**공통 모듈은 객체 또는 함수로 내보내기**
```javascript
// ✅ api.js (샘플: samples/api.js 참고)
const ApiClient = {
  get: async (endpoint, headers) => { /* ... */ },
  post: async (endpoint, body, headers) => { /* ... */ }
};

// ✅ auth.js (샘플: samples/auth.js 참고)
const Auth = {
  saveToken: (token) => { /* ... */ },
  getToken: () => { /* ... */ },
  isAuthenticated: () => { /* ... */ }
};
```

**각 화면의 초기화 함수**
```javascript
// ✅ 화면 로드 시 호출
function initLoginPage() {
  attachEventListeners();
  // 필요한 초기화 로직
}

// HTML에서
<body onload="initLoginPage()">
```

### 3️⃣ 주석 원칙

```javascript
// ✅ WHY만 남기기 (명백한 코드는 생략)
const delay = 300; // UI 반응성 감지 시간

// ❌ WHAT을 설명하는 주석 (코드가 이미 말함)
// token을 로컬스토리지에서 가져온다
const token = localStorage.getItem('token');

// ❌ 너무 상세한 주석
// 1. endpoint를 변수에 저장
// 2. fetch 함수 호출
// 3. 응답을 JSON으로 파싱
```

### 4️⃣ 공통 모듈 활용 강제

**모든 API 호출**
```javascript
// ✅ 공통 api.js를 통해서만 호출
const response = await ApiClient.get('/api/customers/me');

// ❌ fetch 직접 사용 금지 (인증 헤더, 에러 처리 일관성 깨짐)
const response = await fetch('/api/customers/me');
```

**모든 인증 관련 로직**
```javascript
// ✅ 공통 auth.js 사용
if (Auth.isAuthenticated()) {
  // 인증된 사용자
}

// ❌ localStorage 직접 접근 금지
if (localStorage.getItem('token')) {
  // 인증된 사용자
}
```

---

## 📚 공통 모듈 설명

### 1. `config.js` - 서비스 설정
각 마이크로서비스의 baseURL을 중앙에서 관리합니다.
- **용도**: 서비스 URL 정의
- **샘플**: [ui/samples/config.js](../ui/samples/config.js)
- **사용 예**:
  ```javascript
  import { API_BASE_URL } from './common/config.js';
  const endpoint = `${API_BASE_URL.AUTH}/api/auth/login`;
  ```

### 2. `api.js` - HTTP 클라이언트
모든 API 호출을 통합하여 관리합니다.
- **용도**: fetch 래핑, CommonResponse 파싱, 에러 처리, 토큰 자동 첨부
- **샘플**: [ui/samples/api.js](../ui/samples/api.js)
- **사용 예**:
  ```javascript
  const data = await ApiClient.get('/api/customers/me');
  const result = await ApiClient.post('/api/auth/login', { email, password });
  ```

### 3. `auth.js` - 인증 관리
JWT 토큰의 저장, 조회, 검증을 담당합니다.
- **용도**: 토큰 관리, 로그인 상태 확인, 로그아웃
- **샘플**: [ui/samples/auth.js](../ui/samples/auth.js)
- **사용 예**:
  ```javascript
  Auth.saveToken(jwtToken);
  if (Auth.isAuthenticated()) { /* ... */ }
  Auth.clearToken(); // 로그아웃
  ```

---

## 🏢 서비스 객체 (비즈니스 로직)

API를 기능별로 추상화한 서비스 객체입니다. 엔드포인트와 로직을 캡슐화하여 재사용성과 유지보수성을 높입니다.

### 서비스 구조
```
ui/common/services/
├── AuthService.js      # 인증 (로그인, 회원가입, 토큰 재발급)
├── CustomerService.js  # 고객 (프로필, 배차 관리)
└── DriverService.js    # 기사 (프로필, 배차 승인/거절)
```

### 사용 방법

**1. HTML에서 서비스 로드**
```html
<script src="/common/services/AuthService.js"></script>
<script src="/common/services/CustomerService.js"></script>
```

**2. 페이지 로직에서 서비스 사용**
```javascript
// 로그인
const response = await AuthService.login(email, password);
Auth.saveToken(response.token);
Auth.saveRefreshToken(response.refreshToken);

// 프로필 조회
const profile = await CustomerService.getProfile();

// 배차 생성
const dispatch = await CustomerService.createDispatch({
  startLocation: '서울시 강남구',
  endLocation: '서울시 강북구'
});
```

### 서비스별 주요 메서드

**AuthService**
- `login(email, password)` - 로그인
- `register(email, password, role)` - 회원가입
- `refresh()` - 토큰 재발급

**CustomerService**
- `getProfile()` - 프로필 조회
- `registerProfile(profile)` - 프로필 등록
- `updateProfile(profile)` - 프로필 수정
- `createDispatch(dispatch)` - 배차 요청
- `getDispatches(options)` - 내 배차 목록
- `getDispatch(id)` - 배차 상세 조회

**DriverService**
- `getProfile()` - 프로필 조회
- `registerProfile(profile)` - 프로필 등록
- `updateProfile(profile)` - 프로필 수정
- `updateLocation(location)` - 위치 업데이트
- `getPendingDispatches(options)` - Pending 배차 목록
- `acceptDispatch(id)` - 배차 승인
- `rejectDispatch(id, reason)` - 배차 거절
- `getDispatch(id)` - 배차 상세 조회

---

## 🏠 진입점 구현 (`index.html`)

애플리케이션의 진입점으로, 사용자의 인증 상태를 확인하여 적절한 페이지로 라우팅합니다.

**역할:**
1. Auth 상태 확인
2. 미인증 → `/pages/auth/login.html`로 이동
3. 인증됨 → 역할에 따라 대시보드로 이동
   - CUSTOMER 역할 → `/pages/customer/index.html`
   - DRIVER 역할 → `/pages/driver/index.html`

**샘플**: [ui/samples/index.html](../ui/samples/index.html)

```javascript
// 진입점 로직 예시
function initApp() {
  if (Auth.isAuthenticated()) {
    if (Auth.isCustomer()) {
      window.location.href = '/pages/customer/index.html';
    } else if (Auth.isDriver()) {
      window.location.href = '/pages/driver/index.html';
    }
  } else {
    // 미로그인 → 로그인 페이지 링크 표시
    window.location.href = '/pages/auth/login.html';
  }
}

window.addEventListener('load', initApp);
```

---

## 🎨 화면별 구현 패턴

### 절대 경로 사용 가이드

**중요**: 경로 깊이가 다양해지므로 **절대 경로** 사용 (루트 `/`부터 시작)

```
/pages/auth/login.html           (1depth)
/pages/customer/profile.html     (2depth)
/pages/customer/dispatch/list.html (3depth)
```

모두 동일하게 `/common/` 경로로 접근 가능

### 기본 HTML 구조
```html
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>화면 제목</title>
  <!-- ✅ 절대 경로 사용 (모든 깊이에서 동일) -->
  <link rel="stylesheet" href="/styles/main.css">
</head>
<body>
  <!-- 콘텐츠 -->
  
  <!-- ✅ 공통 모듈 로드 (절대 경로) -->
  <script src="/common/config.js"></script>
  <script src="/common/api.js"></script>
  <script src="/common/auth.js"></script>
  
  <!-- 페이지별 로직 (상대 경로 또는 절대 경로 모두 가능) -->
  <script src="./page-logic.js"></script>
  <!-- 또는 -->
  <script src="/pages/customer/profile.js"></script>
</body>
</html>
```

### 화면 로직 패턴
1. **초기화 함수** (`onload`)
2. **이벤트 리스너 부착**
3. **API 호출 함수**
4. **UI 업데이트 함수**

**샘플**: [ui/samples/pages/login.html](../ui/samples/pages/login.html)

---

## 🔄 개발 워크플로우

### 1. 환경 준비
```bash
cd project-root
docker-compose up
# http://localhost:3000 접속
```

### 2. 파일 수정
```
호스트의 ui/pages/login.html 수정
→ 브라우저에서 새로고침 (F5)
→ 변경사항 확인 (컨테이너 재빌드 불필요)
```

### 3. 로컬 테스트
- 브라우저 DevTools 활용 (Console, Network 탭)
- 각 API 호출 및 응답 확인
- 토큰 저장 상태 확인 (DevTools → Application → LocalStorage)

### 4. 커밋
- Git 컨벤션 준수: [03-git-convention.md](./03-git-convention.md) 참고
- 예: `feat(ui): 로그인 페이지 구현`

---

## ⚠️ 에러 처리 및 인증 관련 공통 로직

### API 에러 처리
공통 `api.js`에서 처리하는 사항:
- `CommonResponse` 구조 파싱 (`code`, `message`, `data`)
- HTTP 상태 코드 및 API 응답 코드 확인
- 토큰 만료(401) 시 **자동으로 로그인 페이지로 이동**

### 토큰 만료 시나리오
```
1. API 호출 → 401 Unauthorized 응답
2. api.js에서 감지 → localStorage 토큰 삭제
3. 자동으로 login.html로 이동
4. 사용자는 다시 로그인
```

### 초기 접근 시 인증 확인
```javascript
// index.html에서
if (Auth.isAuthenticated()) {
  // 역할 확인 후 customer.html 또는 driver.html로 이동
} else {
  // login.html로 이동
}
```

---

## 📝 참고 파일

- [TASK-ui-static.md](../TASK-ui-static.md) - 작업 계획서 및 체크리스트
- [03-git-convention.md](./03-git-convention.md) - Git 컨벤션
- `ui/samples/` - 구현 샘플 코드

---

**승인 날짜**: 2026-06-05
