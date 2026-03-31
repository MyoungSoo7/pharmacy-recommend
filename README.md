# 약국 추천 시스템

사용자가 입력한 주소를 기반으로 카카오 지오코딩 API와 Haversine 거리 계산을 활용하여 반경 10km 내 최근접 약국 3곳을 추천하는 웹 애플리케이션.

---

## 프로젝트 소개

주소를 입력하면 카카오 지오코딩 API로 좌표를 변환하고, Haversine 공식으로 거리를 계산하여 10km 내 가장 가까운 약국 3곳을 추천합니다. 추천 결과는 카카오맵 링크로 제공되며, Redis 캐시와 Docker Compose 기반 인프라를 구축했습니다.

![ERD](https://github.com/MyoungSoo7/pharmacyrecommend/assets/13523622/80e9bcc1-0b6b-49f1-9f42-fae832375626)

---

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| 언어 | Java | 17 |
| 프레임워크 | Spring Boot | 3.2 |
| 데이터베이스 | MariaDB | 11.2 |
| 캐시 | Redis | 7 |
| ORM | Spring Data JPA | - |
| 캐시 연동 | Spring Data Redis | - |
| 재시도 | Spring Retry (@Retryable) | - |
| 템플릿 엔진 | Mustache | - |
| 인프라 | Docker, Docker Compose | - |
| 테스트 | Spock (Groovy) | - |

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **주소 지오코딩** | 카카오 지도검색 API로 주소 → 좌표 변환 |
| **거리 계산** | Haversine 공식 기반 10km 내 최근접 약국 3곳 계산 |
| **카카오맵 링크** | 추천 약국의 지도 위치 및 로드 뷰 제공 |
| **단축 URL** | Base62 인코딩 기반 단축 URL 생성 |
| **Redis 캐시** | 약국 데이터 캐싱 (24시간 TTL, 자동 리로드) |
| **API 재시도** | @Retryable 적용 — API 호출 실패 시 자동 재시도 |
| **DDD 설계** | 도메인 주도 설계 (Direction, Pharmacy) |

---

## 빠른 시작

### 사전 요구사항

- Docker & Docker Compose
- 카카오 REST API 키

### 설치 및 실행

```bash
# 프로젝트 클론
git clone https://github.com/MyoungSoo7/pharmacyrecommend.git
cd pharmacyrecommend

# Docker Compose 실행
docker compose up -d
```

- 접속: **http://localhost:8081**

---

## 프로젝트 구조

```
├── direction/          # 약국 방향 안내
│   ├── controller/
│   ├── service/
│   ├── entity/
│   ├── dto/
│   └── repository/
├── pharmacy/           # 약국 데이터 관리
│   ├── controller/
│   ├── service/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   └── cache/          # Redis 캐시
├── api/                # 외부 API 연동
│   ├── service/
│   └── dto/
├── config/             # 설정
└── util/               # 유틸리티
```

---

## 성능 최적화

| 항목 | 설정 |
|------|------|
| Redis 캐시 TTL | 24시간, 자동 리로드 |
| RestTemplate 타임아웃 | 5초 |
| HikariCP | 커넥션 풀 최적화 |
| GZip 압축 | 활성화 |
| DB 인덱스 | 최적화 적용 |

---

## 보안

| 항목 | 구현 |
|------|------|
| API 재시도 | @Retryable (호출 실패 시 대기 후 재시도) |
| 시크릿 관리 | 환경변수 기반 (API 키, DB 비밀번호) |

---

## CI/CD

GitHub Actions를 통한 자동화 파이프라인:

- Gradle 빌드
- Spock 테스트 실행
- Docker 이미지 빌드

---

## 문서 목록

| 문서 | 경로 |
|------|------|
| 기능 명세서 | [`docs/functional-spec.md`](docs/functional-spec.md) |
| 사용자 매뉴얼 | [`docs/manual.md`](docs/manual.md) |
| 업무 프로세스 정의서 | [`docs/process-definition.md`](docs/process-definition.md) |
| Redis 모니터링 가이드 | [`docs/redis-monitoring.md`](docs/redis-monitoring.md) |
| 화면 설계서 | [`docs/screen-design.md`](docs/screen-design.md) |
| 시퀀스 다이어그램 | [`docs/sequence-diagram.md`](docs/sequence-diagram.md) |
| 테스트 리포트 | [`docs/test-report.md`](docs/test-report.md) |
| 문제 해결 가이드 | [`docs/troubleshooting.md`](docs/troubleshooting.md) |

---

## 테스트

4개 테스트 클래스, 15건 이상의 테스트 케이스 (Spock Framework):

```bash
./gradlew test
```

- 카카오 API 연동 테스트
- Haversine 거리 계산 테스트
- Redis 캐시 동작 테스트
- 약국 추천 로직 통합 테스트

---

## 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
