# Line Ending 관리 정책

**상태**: 채택됨  
**이유**: Windows 팀원 지원 + Docker 크로스 플랫폼 호환성

## 정책
```
/gradlew text eol=lf
*.bat text eol=crlf
*.jar binary
```

## 적용 범위
- 모든 팀원 (강제)
- 신규 프로젝트도 동일 적용

## 관련 문서
- troubleshooting/sample-gradle-docker-crlf-error.md
