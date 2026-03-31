# Redis 캐시 모니터링 가이드

## 캐시 히트율 확인

Redis 캐시의 효율성을 판단하는 핵심 지표는 **히트율(Hit Rate)** 입니다.

### 히트율 계산 공식

```
히트율 = keyspace_hits / (keyspace_hits + keyspace_misses) * 100
```

- **keyspace_hits**: 캐시에서 키를 찾은 횟수
- **keyspace_misses**: 캐시에서 키를 찾지 못한 횟수
- 히트율이 **90% 이상**이면 양호, **80% 미만**이면 캐시 전략 재검토 필요

## Redis CLI로 확인

```bash
# Redis 통계 정보 확인
redis-cli INFO stats | grep keyspace

# 출력 예시:
# keyspace_hits:12345
# keyspace_misses:678
```

### Docker 환경에서 확인

```bash
docker exec -it redis redis-cli INFO stats | grep keyspace
```

## Spring Actuator + Micrometer 모니터링

### 권장: Actuator 추가 후 메트릭 모니터링

현재 프로젝트에 `spring-boot-starter-actuator` 의존성이 이미 포함되어 있습니다.

### 설정 추가 (application.yml)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, info
  metrics:
    cache:
      enabled: true
```

### 캐시 메트릭 조회

```bash
# 캐시 히트/미스 메트릭 확인
curl http://localhost:8080/actuator/metrics/cache.gets

# 특정 캐시 이름 필터링
curl http://localhost:8080/actuator/metrics/cache.gets?tag=cache:pharmacyCache
```

### 주요 메트릭

| 메트릭 | 설명 |
|--------|------|
| `cache.gets{result=hit}` | 캐시 히트 횟수 |
| `cache.gets{result=miss}` | 캐시 미스 횟수 |
| `cache.puts` | 캐시 저장 횟수 |
| `cache.evictions` | 캐시 제거 횟수 |

## 모니터링 체크리스트

- [ ] Redis INFO stats로 keyspace_hits/misses 주기적 확인
- [ ] Actuator `/actuator/metrics/cache.gets` 엔드포인트 활성화
- [ ] 히트율 80% 미만 시 TTL 및 캐시 전략 재검토
- [ ] Redis 메모리 사용량 모니터링 (`INFO memory`)
