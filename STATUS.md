# STATUS — Pharmacy Recommend

> 카카오 지오코딩 + Haversine 거리 계산으로 반경 10km 내 최근접 약국 3곳 추천

**Last updated:** 2026-04-09

## 현재 상태
- **활성 브랜치:** `master`
- **스택:** Spring Boot 3.2 / Java 17 / Redis / Docker Compose
- **최근 커밋:** `99ee280` feat: add unit tests + Swagger API docs (unverified)

## 최근 진척
- 단위 테스트 + Swagger API 문서 추가 (검증 필요 표시)
- Claude 에이전트 2개 (약국검색/캐시관리)
- ai-dev-team 커맨드 + 17개 에이전트 프롬프트
- Redis 캐시 히트율 모니터링 가이드
- GitHub Actions CI (빌드+테스트, MariaDB+Redis)
- 사용자 매뉴얼

## 진행 중
- `(unverified)` 표시된 테스트/문서 재검증
- 캐시 히트율 모니터링 지표 대시보드화

## 다음 할 일
- [ ] 지오코딩 API 실패 시 폴백 전략
- [ ] 캐시 TTL 튜닝
- [ ] 부하 테스트(동시 1000 req)

## 주요 위험/메모
- 카카오 API 쿼터/정책 의존
- Redis 장애 시 전체 추천 응답 지연 → 서킷 브레이커 고려

## 참고 문서
- `README.md` — 프로젝트 개요
- `CLAUDE.md` — 에이전트 운용 가이드
- `HARNESS.md` — Claude Code 개발 하네스 구성
