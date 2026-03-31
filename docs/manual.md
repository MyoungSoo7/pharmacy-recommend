# Pharmacy-Recommend 사용자 매뉴얼

## 목차

1. [설치 방법](#설치-방법)
2. [실행 방법](#실행-방법)
3. [사용 가이드](#사용-가이드)
4. [주요 설정](#주요-설정)
5. [FAQ](#faq)

---

## 설치 방법

### 사전 요구사항

Docker Compose를 사용하는 경우와 로컬 환경 직접 설치, 두 가지 방법을 지원합니다.

**방법 1: Docker Compose (권장)**

| 항목 | 버전 | 비고 |
|------|------|------|
| Docker & Docker Compose | 최신 | 전체 서비스 컨테이너 실행 |
| Git | 최신 | |

**방법 2: 로컬 환경 직접 설치**

| 항목 | 버전 | 비고 |
|------|------|------|
| Java JDK | 17 | OpenJDK 또는 Oracle JDK |
| MariaDB | 10.x 이상 | |
| Redis | 7.x | 캐시용 |
| Git | 최신 | |

### 프로젝트 클론

```bash
git clone <repository-url>
cd pharmacy-recommend
```

### 환경 변수 설정

`.env` 파일을 생성하고 필요한 설정을 입력합니다:

```bash
cp .env.example .env
# .env 파일을 편집하여 API 키 등 설정
```

---

## 실행 방법

### 방법 1: Docker Compose (권장)

```bash
docker compose up -d
```

모든 서비스(애플리케이션, MariaDB, Redis)가 한 번에 시작됩니다.

### 방법 2: 로컬 실행

```bash
# MariaDB, Redis가 실행 중인지 확인 후
./gradlew bootRun
```

### 실행 확인

브라우저에서 `http://localhost:8081`에 접속합니다.

---

## 사용 가이드

### 기본 사용 흐름

1. **주소 입력**: 메인 페이지에서 현재 위치 또는 원하는 주소 입력
2. **검색**: 검색 버튼 클릭
3. **결과 확인**: 입력한 주소 기준 가까운 약국 3곳이 거리순으로 표시
4. **카카오맵 연결**: 각 약국을 클릭하면 카카오맵에서 상세 위치 확인 가능

### 주요 기능

| 기능 | 설명 |
|------|------|
| 주소 기반 약국 검색 | 입력 주소에서 가장 가까운 약국 3곳 추천 |
| 거리 계산 | Haversine 공식 기반 직선 거리 계산 |
| 카카오맵 연동 | 약국 위치를 카카오맵에서 확인 |
| 결과 캐싱 | Redis를 활용한 검색 결과 캐싱 |

### 약국 데이터 초기 로딩

최초 실행 시 약국 데이터를 로딩해야 합니다:

```
GET http://localhost:8081/csv/save
```

이 엔드포인트를 호출하면 CSV 파일의 약국 데이터가 데이터베이스에 저장됩니다.

---

## 주요 설정

### .env

```env
# MariaDB
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/pharmacy
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# 카카오 REST API
KAKAO_REST_API_KEY=your_kakao_rest_api_key
```

### 설정 항목 설명

| 항목 | 설명 |
|------|------|
| `SPRING_DATASOURCE_*` | MariaDB 접속 정보 |
| `SPRING_REDIS_*` | Redis 접속 정보 (캐시 서버) |
| `KAKAO_REST_API_KEY` | 카카오 주소 검색 및 지도 API 키 |

---

## FAQ

### Q: 카카오 API 키는 어떻게 발급받나요?

**A:** 다음 절차를 따릅니다:

1. [카카오 개발자](https://developers.kakao.com/)에 로그인
2. **내 애플리케이션** → **애플리케이션 추가하기**
3. 앱 생성 후 **앱 키** 메뉴에서 **REST API 키** 확인
4. **플랫폼** 설정에서 **Web** 플랫폼 추가 (도메인: `http://localhost:8081`)
5. `.env` 파일의 `KAKAO_REST_API_KEY`에 REST API 키 입력

### Q: 약국 데이터 초기 로딩은 어떻게 하나요?

**A:** 애플리케이션 실행 후 다음 URL을 브라우저 또는 API 클라이언트에서 호출합니다:

```
GET http://localhost:8081/csv/save
```

이 요청은 프로젝트에 포함된 CSV 파일에서 전국 약국 데이터를 읽어 데이터베이스에 저장합니다. 최초 1회만 실행하면 됩니다.

> 데이터 로딩에는 수 분이 소요될 수 있습니다. 완료 메시지를 확인한 후 검색 기능을 사용하세요.

### Q: Redis 캐시를 수동으로 갱신하려면 어떻게 하나요?

**A:** Redis 캐시를 초기화하려면 Redis CLI를 사용합니다:

```bash
# Docker 환경
docker compose exec redis redis-cli FLUSHALL

# 로컬 환경
redis-cli FLUSHALL
```

또는 특정 키만 삭제하려면:

```bash
redis-cli KEYS "pharmacy:*"
redis-cli DEL <키이름>
```

캐시 초기화 후 다음 검색 요청 시 새로운 데이터로 캐시가 재생성됩니다.
