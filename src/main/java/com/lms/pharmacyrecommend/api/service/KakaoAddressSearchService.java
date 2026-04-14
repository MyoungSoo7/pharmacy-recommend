package com.lms.pharmacyrecommend.api.service;

import com.lms.pharmacyrecommend.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final KakaoUriBuilderService kakaoUriBuilderService;
    private final RestTemplate restTemplate;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
            " AppleWebKit/537.36 (KHTML, like Gecko)" +
            " Chrome/54.0.2840.99 Safari/537.36";

    // 주소 검색 결과 캐시 (같은 주소 반복 API 호출 방지)
    private final Map<String, CachedResponse> addressCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60 * 60 * 1000L; // 1시간
    private static final int MAX_CACHE_SIZE = 500;

    private record CachedResponse(KakaoApiResponseDto response, long cachedAt) {}

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000)
    )
    public KakaoApiResponseDto requestAddressSearch(String address) {

        if(ObjectUtils.isEmpty(address)) return null;

        // 캐시 확인
        CachedResponse cached = addressCache.get(address);
        if (cached != null && System.currentTimeMillis() - cached.cachedAt() < CACHE_TTL_MS) {
            log.info("[KakaoAddressSearchService] cache hit: {}", address);
            return cached.response();
        }

        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);
        HttpEntity<?> requestEntity = createHttpEntityWithHeaders();

        KakaoApiResponseDto response = fetchKakaoApiResponse(uri, requestEntity);

        // 캐시 저장
        if (response != null) {
            if (addressCache.size() >= MAX_CACHE_SIZE) {
                evictExpiredEntries();
            }
            addressCache.put(address, new CachedResponse(response, System.currentTimeMillis()));
        }

        return response;
    }


    private HttpEntity<?> createHttpEntityWithHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);

        return new HttpEntity<>(headers);
    }

    private KakaoApiResponseDto fetchKakaoApiResponse(URI uri, HttpEntity<?> requestEntity) {
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, KakaoApiResponseDto.class).getBody();
    }

    @Recover
    public KakaoApiResponseDto recover(RuntimeException e, String address) {
        log.error("All the retries failed. address: {}, error : {}", address, e.getMessage(), e);
        return null;
    }

    private void evictExpiredEntries() {
        long now = System.currentTimeMillis();
        addressCache.entrySet().removeIf(entry ->
                now - entry.getValue().cachedAt() > CACHE_TTL_MS);
    }
}
