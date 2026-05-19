# Docker에서 Gradle 실행 오류

## 증상
```
/bin/sh: ./gradlew: /bin/bash^M: bad interpreter
```

## 원인
Windows의 `core.autocrlf=true`가 LF를 CRLF로 변환 → Linux에서 실행 불가

## 해결
`.gitattributes` 추가:
```
/gradlew text eol=lf
```

## 체크리스트
- [x] 파일 CRLF 확인
- [x] .gitattributes 적용
- [x] Docker 재빌드 성공

## 최종 결정
→ `decisions/sample-line-ending-policy.md`
