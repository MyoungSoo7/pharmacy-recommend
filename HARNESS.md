# HARNESS — Pharmacy Recommend

> Claude Code 개발 하네스 구성 — 지오코딩/캐시 도메인 전용 에이전트

**Last updated:** 2026-04-09

## 목적
카카오 지오코딩 API 의존 + Redis 캐시 중심 시스템이라, **외부 API 장애 내구성**과 **캐시 히트율**이 운영 핵심 지표다. 전용 에이전트로 도메인 지식을 집중시킨다.

## 디렉토리 구조
```
.claude/
├── agents/
│   ├── cache-manager.md     # Redis 캐시 전략·TTL·히트율 모니터링
│   └── pharmacy-search.md   # 약국 검색 도메인 (Haversine·10km 반경)
└── commands/
    ├── agents/
    └── ai-dev-team.md
```

## 에이전트 사용 원칙
1. **검색 알고리즘/도메인 변경** → `pharmacy-search`
2. **캐시 전략/TTL 조정** → `cache-manager` (히트율 측정 가이드 포함)

## 커맨드
- `/ai-dev-team` — 역할 기반 산출물 일괄 생성

## 확장 가이드
- 카카오 API 장애 시 **폴백 전략**(다른 지오코딩 제공자, 레거시 좌표 DB 등) 추가 시 별도 에이전트 분리 검토
- 서킷 브레이커 도입하면 `resilience-reviewer` 신규 생성 권장

## 관련 문서
- `CLAUDE.md` — 에이전트 운용 규칙
- `STATUS.md` — 현재 진행 상황 (테스트 검증 중)
- `README.md` — 프로젝트 개요
- `docs/` — Redis 캐시 히트율 모니터링 가이드
