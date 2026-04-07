# 약국 추천 시스템

## 프로젝트 개요

사용자가 입력한 주소를 기반으로 카카오 지오코딩 API와 Haversine 거리 계산을 활용하여 반경 10km 내 최근접 약국 3곳을 추천하는 웹 애플리케이션.

## 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.2
- **데이터베이스**: MariaDB
- **캐시**: Redis 7
- **템플릿 엔진**: Mustache
- **인프라**: Docker Compose
- **테스트**: Spock (Groovy)

## 주요 기능

- 주소 입력 후 카카오 지오코딩 API를 통한 좌표 변환
- Haversine 공식 기반 거리 계산으로 10km 내 최근접 약국 3곳 추천
- Base62 인코딩 기반 단축 URL 생성
- Redis 캐시를 활용한 약국 데이터 관리 (24시간 TTL)
- API 호출 실패 시 Retry 처리

## 패키지 구조

```
├── direction/          # 약국 방향 안내
│   ├── controller
│   ├── service
│   ├── entity
│   ├── dto
│   └── repository
├── pharmacy/           # 약국 데이터 관리
│   ├── controller
│   ├── service
│   ├── entity
│   ├── dto
│   ├── repository
│   └── cache          # Redis 캐시
├── api/                # 외부 API 연동
│   ├── service
│   └── dto
├── config/             # 설정
└── util/               # 유틸리티
```

## 인프라

### Docker Compose 구성

- **MariaDB 11.2**: 약국 및 방향 데이터 저장
- **Redis 6**: 약국 데이터 캐시
- **App**: multi-stage Dockerfile 빌드

### 실행 방법

```bash
docker-compose up -d
```

## 보안

- Spring Data Redis 연동
- 환경변수 기반 시크릿 관리 (API 키, DB 비밀번호 등)

## 성능 최적화

- HikariCP 커넥션 풀
- RestTemplate 타임아웃: 5초
- Redis TTL: 24시간, 캐시 자동 리로드
- GZip 압축 활성화
- DB 인덱스 최적화








## 프로젝트 현황

- **최종 갱신**: 2026-04-07 09:22
- **상태**: 활성
- **테스트 파일 수**: 0
- **최근 커밋**:
- d904199 feat: ai-dev-team 커맨드 + 에이전트 프롬프트 17개 추가 (7일 전)
- 02f9a5b docs: README.md 전면 업데이트 (기술스택/기능/빠른시작/문서/테스트) (7일 전)
- 218641f feat: Claude 에이전트 2개 추가 (약국검색/캐시관리) (7일 전)

> 전체 현황: [STATUS.md](/Users/lms/inter-asat/docs/STATUS.md)
