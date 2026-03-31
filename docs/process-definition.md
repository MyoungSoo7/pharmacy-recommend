# 프로세스정의서 — 약국 추천 시스템

## 1. 약국 추천 프로세스

### 1.1 프로세스 개요

| 항목 | 내용 |
|------|------|
| 프로세스명 | 약국 추천 프로세스 |
| 트리거 | 사용자가 주소를 입력하고 "약국 찾기" 버튼 클릭 |
| 입력 | 도로명 주소 (문자열) |
| 출력 | 가장 가까운 약국 3곳 정보 + 카카오맵 단축 URL |
| 관련 API | 카카오 지오코딩 API, Redis, DB |

### 1.2 프로세스 흐름도

```mermaid
flowchart TD
    A[사용자: 주소 입력] --> B[카카오 지오코딩 API 호출]
    B --> C{API 응답 성공?}
    C -->|실패| D[에러 메시지 표시 - 주소 변환 실패]
    C -->|성공| E[위도/경도 추출]
    E --> F[Redis에서 약국 데이터 조회]
    F --> G{캐시 히트?}
    G -->|히트| H[Redis 약국 목록 사용]
    G -->|미스| I[DB에서 약국 데이터 조회]
    I --> J[Redis에 자동 적재 - 24h TTL]
    J --> H
    H --> K[Haversine 공식으로 거리 계산]
    K --> L[10km 이내 약국 필터링]
    L --> M{필터 결과 존재?}
    M -->|없음| N[가까운 약국 없음 안내]
    M -->|있음| O[거리순 정렬 → 상위 3곳 선택]
    O --> P[Direction 엔티티 저장]
    P --> Q[Base62 인코딩 → 단축 URL 생성]
    Q --> R[결과 페이지 렌더링]
```

### 1.3 상세 처리 규칙

| 단계 | 처리 내용 | 비고 |
|------|-----------|------|
| 카카오 지오코딩 | `GET https://dapi.kakao.com/v2/local/search/address.json?query={주소}` | REST API KEY 헤더 인증 |
| Haversine 공식 | `d = 2r * arcsin(sqrt(sin²(Δlat/2) + cos(lat1)*cos(lat2)*sin²(Δlon/2)))` | 지구 반지름 6371km 사용 |
| 10km 필터 | 직선 거리 기준, 반경 10km 초과 약국 제외 | 설정값 변경 가능 |
| Direction 저장 | 입력 주소, 입력 위경도, 약국명, 약국 주소, 약국 위경도 | 추적/통계 목적 |
| Base62 인코딩 | Direction PK(Long) → Base62 문자열 | URL 단축 목적, 영문 대소문자+숫자 조합 |

### 1.4 Haversine 거리 계산 상세

```mermaid
flowchart LR
    A[입력 위도/경도] --> B[약국 위도/경도]
    A --> C[위도 차이 Δlat]
    A --> D[경도 차이 Δlon]
    C --> E["a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)"]
    D --> E
    E --> F["c = 2 * atan2(√a, √(1-a))"]
    F --> G["거리 = 6371 * c (km)"]
```

---

## 2. 캐시 관리 프로세스

### 2.1 프로세스 개요

| 항목 | 내용 |
|------|------|
| 프로세스명 | 캐시 관리 프로세스 |
| 트리거 | 약국 추천 시 Redis 캐시 미스 또는 관리자 수동 호출 |
| 입력 | DB의 약국 데이터 |
| 출력 | Redis에 약국 데이터 적재 |
| 관련 기술 | Redis, Spring Data JPA |

### 2.2 프로세스 흐름도

```mermaid
flowchart TD
    subgraph 자동_캐시_관리
        A1[약국 추천 요청 수신] --> B1[Redis에서 약국 데이터 조회]
        B1 --> C1{캐시 히트?}
        C1 -->|히트| D1[캐시 데이터 반환]
        C1 -->|미스| E1[DB에서 전체 약국 데이터 조회]
        E1 --> F1[Redis에 약국 데이터 저장]
        F1 --> G1[TTL 24시간 설정]
        G1 --> D1
    end

    subgraph 수동_캐시_웜업
        A2["관리자: GET /csv/save 호출"] --> B2[DB에서 약국 데이터 조회]
        B2 --> C2[Redis에 전체 데이터 적재]
        C2 --> D2[TTL 24시간 설정]
        D2 --> E2[적재 완료 응답]
    end
```

### 2.3 상세 처리 규칙

| 단계 | 처리 내용 | 비고 |
|------|-----------|------|
| Redis 조회 | `RedisTemplate.opsForList()` 또는 Hash 구조 | 약국 전체 목록을 하나의 키로 관리 |
| DB Fallback | `PharmacyRepository.findAll()` | 캐시 미스 시 자동 실행 |
| Redis 적재 | 약국 ID, 이름, 주소, 위도, 경도 저장 | JSON 직렬화 |
| TTL | 24시간(86400초) | 만료 후 다음 요청 시 자동 재적재 |
| 수동 웜업 | 서비스 초기 기동 또는 데이터 갱신 시 사용 | `/csv/save` 엔드포인트 |

### 2.4 캐시 상태 다이어그램

```mermaid
stateDiagram-v2
    [*] --> 캐시없음: 서비스 시작
    캐시없음 --> 캐시적재: DB 조회 후 Redis 저장
    캐시적재 --> 캐시히트: 요청 수신
    캐시히트 --> 캐시적재: 데이터 유효
    캐시적재 --> 캐시만료: TTL 24h 경과
    캐시만료 --> 캐시없음: 자동 삭제
    캐시없음 --> 캐시적재: 수동 웜업 (/csv/save)
```

---

## 3. 방향 리다이렉트 프로세스

### 3.1 프로세스 개요

| 항목 | 내용 |
|------|------|
| 프로세스명 | 방향 리다이렉트 프로세스 |
| 트리거 | 사용자가 결과 페이지에서 카카오맵 링크 클릭 |
| 입력 | Base62 인코딩된 Direction ID |
| 출력 | 카카오맵 URL로 302 리다이렉트 |

### 3.2 프로세스 흐름도

```mermaid
flowchart TD
    A["사용자: /dir/{encodedId} 접근"] --> B[Base62 디코드]
    B --> C[Direction ID 추출 - Long]
    C --> D[Direction 엔티티 조회]
    D --> E{Direction 존재?}
    E -->|아니오| F[404 Not Found]
    E -->|예| G[약국 위도/경도 추출]
    G --> H[카카오맵 URL 조립]
    H --> I["https://map.kakao.com/link/map/{약국명},{위도},{경도}"]
    I --> J[302 Redirect 응답]
    J --> K[사용자 브라우저 → 카카오맵 이동]
```

### 3.3 상세 처리 규칙

| 단계 | 처리 내용 | 비고 |
|------|-----------|------|
| Base62 디코드 | encodedId 문자열 → Long 타입 PK 변환 | `[0-9a-zA-Z]` 62개 문자 사용 |
| Direction 조회 | `DirectionRepository.findById(id)` | 없으면 404 반환 |
| URL 조립 | 약국명, 위도, 경도를 카카오맵 URL 템플릿에 삽입 | URL 인코딩 처리 |
| 리다이렉트 | HTTP 302 Found, Location 헤더에 카카오맵 URL | 브라우저가 자동 이동 |

### 3.4 Base62 인코딩/디코딩 흐름

```mermaid
flowchart LR
    subgraph 인코딩_약국_추천_시
        A[Direction PK: 12345] --> B["Base62 인코딩"]
        B --> C["encodedId: dnh"]
        C --> D["단축 URL: /dir/dnh"]
    end

    subgraph 디코딩_리다이렉트_시
        E["요청: /dir/dnh"] --> F["Base62 디코딩"]
        F --> G["Direction PK: 12345"]
        G --> H["Direction 엔티티 조회"]
    end
```
