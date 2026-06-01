# 3️⃣ Git 컨벤션 및 규칙 정의

**상태**: 완료  
**마지막 업데이트**: 2026-05-16

---

## 🔀 브랜치 전략: Trunk-Based Development

### 브랜치 구조

```
main (항상 배포 가능한 상태)
  ↑ merge (1~3일 후, PR 필수)
  │
feature/<service>/<feature-name>
  ├─ feature/account/login
  ├─ feature/account/password-reset
  ├─ feature/customer/profile-update
  ├─ feature/driver/registration
  └─ feature/dispatcher/matching
```

### 브랜치 규칙

**Main 브랜치**
- 항상 배포 가능한 코드만 존재
- 직접 push 금지
- PR + 리뷰 필수
- 자동 테스트 통과 필수

**Feature 브랜치**
- 네이밍: `feature/<service>/<feature-name>`
- 유지 기간: 1~3일 (최대 1주)
- Merge 후 삭제

---

## 📝 커밋 메시지: Conventional Commits

### 형식

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 정의

| Type | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 | `feat(account): 회원가입 API` |
| `fix` | 버그 수정 | `fix(dispatcher): 배차 오류 처리` |
| `docs` | 문서 수정 | `docs: README 업데이트` |
| `style` | 포매팅, 공백 등 | `style: 불필요한 공백 제거` |
| `refactor` | 코드 리팩토링 | `refactor(account): 로직 단순화` |
| `test` | 테스트 추가/수정 | `test(driver): 조회 테스트` |
| `chore` | 빌드, 의존성 등 | `chore: Gradle 업데이트` |
| `ci` | CI/CD 설정 | `ci: GitHub Actions 수정` |

### Scope 정의

```
account    - Account Service
customer   - Customer Service
driver     - Driver Service
dispatcher - Dispatcher Service
common     - Common Library
domain     - Domain Models
infra      - Infrastructure
docs       - Documentation
```

### 예시

```
feat(account): 회원가입 API 구현

POST /api/accounts/signup 엔드포인트 추가
- 이메일 중복 검증
- 비밀번호 암호화

Closes #123
```

---

## 🔄 Pull Request (PR) 프로세스

### PR 제목
- Conventional Commits 형식과 동일
- 예: `feat(account): 회원가입 API 구현`

### PR 검토
- **최소 리뷰어**: 1명
- **자동 테스트**: PR 생성 시 자동 실행
- **승인 조건**: 
  - [ ] 1명 이상 승인
  - [ ] 자동 테스트 통과
  - [ ] CI/CD 파이프라인 통과

### PR Merge
- Squash Merge 권장 (여러 커밋 → 1개 커밋)
- Merge 후 feature 브랜치 자동 삭제

---

## 🛠️ 로컬 워크플로우

### 1. Feature 브랜치 생성

```bash
git checkout main
git pull origin main
git checkout -b feature/account/login
```

### 2. 개발 및 커밋

```bash
# 파일 수정 후
git add .
git commit -m "feat(account): 로그인 API 구현"
```

### 3. Push 및 PR 생성

```bash
git push origin feature/account/login
# GitHub에서 PR 생성
```

### 4. 리뷰 및 Merge

- 리뷰어의 승인 후 merge
- Main으로 자동 merge

---

## ✅ 확인 사항

- [ ] 팀원 모두 Conventional Commits 이해
- [ ] GitHub main 브랜치 보호 규칙 설정
- [ ] GitHub Actions CI/CD 설정
- [ ] PR 템플릿 설정

---

**승인 날짜**: 2026-05-16
