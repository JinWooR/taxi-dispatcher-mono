# 2️⃣ 모노레포 프로젝트 구조 정의

**상태**: 완료  
**마지막 업데이트**: 2026-05-16

---

## 🏗️ 모노레포 전체 구조

```
taxi-dispatcher-mono/
├── README.md
├── build.gradle                  # Gradle 부모 설정
├── settings.gradle               # Gradle 모듈 설정
├── gradle.properties
├── docker-compose.yml            # 로컬 개발 환경
├── .gitignore
├── .github/                      # GitHub 설정
├── docs/                         # 문서
│   ├── 01-technology-stack.md
│   ├── 02-monorepo-structure.md
│   ├── 03-git-convention.md
│   ├── 04-build-run.md
│   ├── 05-api-common-rules.md
│   └── 06-database-schema.md
├── services/                     # 마이크로서비스
│   ├── account-service/
│   ├── customer-service/
│   ├── driver-service/
│   └── dispatcher-service/
├── shared/                       # 공유 라이브러리
│   ├── common-lib/
│   └── domain-models/
└── infra/                        # 인프라
    ├── docker/
    └── mysql/
```

---

## 📦 마이크로서비스 구성

### 초기 4개 서비스

| 서비스 | 역할 |
|--------|------|
| **account-service** | 계정 관리 (회원가입, 로그인, 비밀번호 등) |
| **customer-service** | 사용자 정보 및 프로필 관리 |
| **driver-service** | 택시 기사 정보 및 상태 관리 |
| **dispatcher-service** | 배차 요청 및 배치 관리 |

### 공유 라이브러리

| 라이브러리 | 역할 |
|------------|------|
| **common-lib** | 공통 예외, 상수, API 응답 포맷 |
| **domain-models** | 공유 도메인 모델, Enum |

---

## 📊 데이터베이스 전략

### 단일 MySQL 인스턴스, 서비스별 스키마 분리

```sql
CREATE SCHEMA account_db;      -- Account Service
CREATE SCHEMA customer_db;     -- Customer Service
CREATE SCHEMA driver_db;       -- Driver Service
CREATE SCHEMA dispatcher_db;   -- Dispatcher Service
```

---

## 📋 모듈 간 의존성

| 서비스 | 의존 대상 |
|--------|----------|
| account-service | common-lib, domain-models |
| customer-service | common-lib, domain-models |
| driver-service | common-lib, domain-models |
| dispatcher-service | common-lib, domain-models |
| common-lib | (없음) |
| domain-models | (없음) |

---

## 🔄 Gradle 부모 설정 기본 구조

```gradle
plugins {
    id 'org.springframework.boot' version '3.5.14' apply false
    id 'io.spring.dependency-management' version '1.1.4' apply false
}

group = 'com.taxi.dispatcher'
version = '1.0.0-SNAPSHOT'

subprojects {
    apply plugin: 'java'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

    java {
        sourceCompatibility = '17'
        targetCompatibility = '17'
    }

    repositories {
        mavenCentral()
    }
}
```

---

## ✅ 확인 사항

- [ ] 모노레포 디렉토리 구조 생성
- [ ] 부모 build.gradle 작성
- [ ] 각 서비스 모듈 생성
- [ ] 공유 라이브러리 모듈 생성
- [ ] MySQL 스키마 생성
- [ ] docker-compose.yml 작성

---

**승인 날짜**: 2026-05-16
