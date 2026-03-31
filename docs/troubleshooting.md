# 트러블슈팅 가이드

## 1. 카카오 API 키 미설정

**증상:** 주소 검색 시 401 Unauthorized 또는 NullPointerException 발생

**해결 방법:**
- `KAKAO_REST_API_KEY` 환경변수가 설정되어 있는지 확인
- 카카오 개발자 콘솔에서 REST API 키를 발급받아 환경변수에 등록
- `application.yml`에서 `${KAKAO_REST_API_KEY}` 참조가 올바른지 확인

---

## 2. Redis 연결 실패

**증상:** 서버 시작 시 `RedisConnectionFailureException` 발생

**해결 방법:**
- Docker Compose로 Redis 컨테이너를 실행: `docker compose up -d redis`
- Redis 포트가 **6379**로 올바르게 바인딩되어 있는지 확인
- `redis-cli ping` 명령으로 연결 상태 확인

---

## 3. 약국 데이터 없음

**증상:** 약국 추천 결과가 0건으로 반환됨

**해결 방법:**
- `/csv/save` 엔드포인트를 호출하여 CSV 데이터를 DB에 로딩하고 Redis에 캐싱
- 캐시 미스 시 자동으로 DB에서 리로드되므로, DB에 약국 데이터가 존재하는지 확인
- 약국 CSV 파일이 `resources` 디렉토리에 존재하는지 확인

---

## 4. RestTemplate 타임아웃

**증상:** 카카오 API 호출 시 `ResourceAccessException` (타임아웃) 발생

**해결 방법:**
- connect/read 타임아웃이 각각 **5초**로 설정되어 있음
- 카카오 API 서버 상태를 확인: https://developers.kakao.com/
- 네트워크 환경(방화벽, 프록시)이 외부 API 호출을 차단하고 있지 않은지 확인

---

## 5. Docker Compose 시작 실패

**증상:** `docker compose up` 실행 시 환경변수 관련 에러 발생

**해결 방법:**
- 프로젝트 루트에 `.env` 파일을 생성하고 필요한 환경변수를 설정
  - `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_PASSWORD` 등
- `docker-compose.yml`에서 참조하는 환경변수 목록을 확인하고 모두 `.env`에 정의

---

## 6. MariaDB 연결 실패

**증상:** `Communications link failure` 또는 연결 거부 에러 발생

**해결 방법:**
- MariaDB 포트가 **3307**인지 확인 (기본 포트 3306이 아님에 주의)
- character-set이 **utf8mb4**로 설정되어 있는지 확인
- MariaDB 서비스가 실행 중인지 확인: `docker ps` 또는 `systemctl status mariadb`

---

## 7. Haversine 거리 계산 오류

**증상:** 약국 추천 결과의 거리가 비정상적으로 크거나 작음

**해결 방법:**
- 위도/경도 순서가 올바른지 확인: **latitude = y축, longitude = x축**
- 카카오 API 응답에서 `x`가 경도(longitude), `y`가 위도(latitude)임을 확인
- 거리 단위가 km인지 확인하고, 반경 설정값과 비교
