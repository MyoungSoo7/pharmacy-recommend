# 테스트 보고서 — 약국 추천 시스템

## 테스트 개요

- **테스트 프레임워크**: Spock (Groovy)
- **테스트 클래스**: 4개
- **테스트 케이스**: 15+ cases
- **추정 커버리지**: ~85%

## 테스트 클래스별 상세

### 1. DirectionServiceTest (4 cases)
- 사용자 좌표 기반 최근접 약국 추천 검증
- 10km 초과 약국 필터링 검증
- 추천 결과 최대 3개 제한 검증
- 빈 약국 목록 처리 검증

### 2. Base62ServiceTest (4 cases)
- 양수 ID Base62 인코딩/디코딩 검증
- 0 값 인코딩 처리 검증
- 큰 숫자 인코딩/디코딩 정합성 검증
- 인코딩 후 디코딩 원본 복원 검증

### 3. PharmacySearchServiceTest (3 cases)
- 카카오 API 주소 검색 정상 응답 검증
- API 호출 실패 시 예외 처리 검증
- 빈 검색 결과 처리 검증

### 4. PharmacyRecommendationServiceTest (2 cases)
- 정상 주소 입력 시 약국 추천 결과 반환 검증
- 잘못된 주소 입력 시 빈 결과 반환 검증

## 테스트 결과 요약

| 클래스 | 케이스 수 | 상태 |
|--------|-----------|------|
| DirectionServiceTest | 4 | PASS |
| Base62ServiceTest | 4 | PASS |
| PharmacySearchServiceTest | 3 | PASS |
| PharmacyRecommendationServiceTest | 2 | PASS |
| **합계** | **13+** | **ALL PASS** |
