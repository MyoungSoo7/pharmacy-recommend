# 약국 추천 시스템

사용자 주소 기반 카카오 지오코딩 + Haversine 거리 계산으로 반경 10km 내 최근접 약국 3곳을 추천하는 웹 애플리케이션

## 프로젝트 소개

주소를 입력하면 카카오 지오코딩 API로 좌표를 변환하고, Haversine 공식으로 거리를 계산하여 10km 내 가장 가까운 약국 3곳을 추천합니다. 추천 결과는 카카오맵 링크로 제공되며, Redis 캐시와 Docker Compose 기반 인프라를 구축했습니다.

![ERD](https://github.com/MyoungSoo7/pharmacyrecommend/assets/13523622/80e9bcc1-0b6b-49f1-9f42-fae832375626)

---

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| Backend | Java + Spring Boot + JPA | 25 / 4.0.4 |
| Database | MariaDB | 11.2 |
| Cache | Redis | 7 |
| 재시도 | Spring Retry (@Retryable) | - |
| 템플릿 엔진 | Mustache | - |
| Infra | Docker Compose | - |

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **주소 지오코딩** | 카카오 지도검색 API로 주소 -> 좌표 변환 |
| **거리 계산** | Haversine 공식 기반 10km 내 최근접 약국 3곳 |
| **카카오맵 링크** | 추천 약국의 지도 위치 및 로드 뷰 제공 |
| **단축 URL** | Base62 인코딩 기반 단축 URL 생성 |
| **Redis 캐시** | 약국 데이터 캐싱 (24시간 TTL, 자동 리로드) |
| **API 재시도** | @Retryable 적용 (호출 실패 시 자동 재시도) |

---

## 빠른 시작

### 사전 요구사항

- Docker & Docker Compose
- 카카오 REST API 키

### 실행

```bash
git clone https://github.com/MyoungSoo7/pharmacyrecommend.git
cd pharmacyrecommend
docker compose up -d
```

접속: **http://localhost:8081**

---

## 프로젝트 구조

```
pharmacy-recommend/
├── direction/          # 약국 방향 안내
│   ├── controller/
│   ├── service/
│   ├── entity/
│   └── repository/
├── pharmacy/           # 약국 데이터 관리
│   ├── controller/
│   ├── service/
│   ├── cache/          # Redis 캐시
│   └── repository/
├── api/                # 외부 API 연동 (카카오)
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

---

## 문서

| 문서 | 경로 |
|------|------|
| 기능 명세서 | [`docs/functional-spec.md`](docs/functional-spec.md) |
| Redis 모니터링 | [`docs/redis-monitoring.md`](docs/redis-monitoring.md) |
| 시퀀스 다이어그램 | [`docs/sequence-diagram.md`](docs/sequence-diagram.md) |
| 테스트 리포트 | [`docs/test-report.md`](docs/test-report.md) |

---

## 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
