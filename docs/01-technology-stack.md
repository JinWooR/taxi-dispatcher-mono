# 1️⃣ 기술 스택 정의

**상태**: 완료  
**마지막 업데이트**: 2026-05-17

---

## 🛠️ 확정된 기술 스택

| 항목 | 기술 | 버전 | 비고 |
|------|------|------|------|
| **Java** | Eclipse Temurin (OpenJDK) | 17 LTS | `eclipse-temurin:17-jdk-slim` |
| **Language Support** | Groovy | - | - |
| **Framework** | Spring Boot | 3.5.14 | - |
| **Build Tool** | Gradle | 최신 | - |
| **Database** | MySQL | 8.0 | - |
| **ORM** | JPA/Hibernate | Spring Boot 기본값 | - |
| **Container** | Docker | 최신 | - |
| **Container Orchestration** | Docker Compose | 최신 | 로컬 개발 환경 |
| **API Documentation** | Springdoc OpenAPI | 2.x | Swagger UI |
| **Serialization** | Jackson | 기본값 | JSON 변환 |
| **Utilities** | Lombok | 최신 | 보일러플레이트 감소 |
| **Logging** | SLF4J + Logback | 기본값 | - |

---

## 📋 향후 도입 예정 기술

### Phase 2: Kubernetes 프로덕션 배포

| 기술 | 용도 |
|------|------|
| **Kubernetes** | 컨테이너 오케스트레이션 및 프로덕션 배포 |

**시기**: 개발 환경 안정화 후, 실제 동작 확인

---

### Phase 3: Kafka 기반 이벤트 아키텍처 진화

| 기술 | 용도 |
|------|------|
| **Apache Kafka** | 서비스 간 비동기 메시징, 의존성 제거 |

**시기**: Kubernetes 배포 확인 후, 이벤트 기반 전환

---

### Phase 4: 관찰성(Observability) 구성

| 기술 | 용도 |
|------|------|
| **Prometheus** | 메트릭 수집 |
| **Grafana** | 메트릭 시각화 |
| **ELK Stack / Loki** | 중앙집중식 로깅 |
| **Jaeger / Zipkin** | 분산 추적(Distributed Tracing) |

**시기**: Kafka 전환 완료 후, 관찰성 강화

---

## 📝 미결정 항목

- **Database Migration Tool**: Flyway vs Liquibase (추후 결정)

---

## ✅ 검증 체크리스트

- [x] Java 17 확정
- [x] Spring Boot 3.5.14 확정
- [x] Gradle 선택 확정
- [x] MySQL 8.0 확정
- [ ] 팀원 환경 설정 완료

---

**승인 날짜**: 2026-05-16
