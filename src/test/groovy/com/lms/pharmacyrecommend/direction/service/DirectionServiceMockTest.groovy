package com.lms.pharmacyrecommend.direction.service

import com.lms.pharmacyrecommend.api.dto.DocumentDto
import com.lms.pharmacyrecommend.api.dto.KakaoApiResponseDto
import com.lms.pharmacyrecommend.api.service.KakaoCategorySearchService
import com.lms.pharmacyrecommend.direction.entity.Direction
import com.lms.pharmacyrecommend.direction.repository.DirectionRepository
import com.lms.pharmacyrecommend.pharmacy.dto.PharmacyDto
import com.lms.pharmacyrecommend.pharmacy.service.PharmacySearchService
import spock.lang.Specification

class DirectionServiceMockTest extends Specification {

    PharmacySearchService pharmacySearchService = Mock()
    DirectionRepository directionRepository = Mock()
    Base62Service base62Service = Mock()
    KakaoCategorySearchService kakaoCategorySearchService = Mock()

    DirectionService directionService = new DirectionService(
            pharmacySearchService, directionRepository, base62Service, kakaoCategorySearchService
    )

    def "buildDirectionList: 10km 이상 약국은 필터되고 거리순 상위 3개만 반환"() {
        given: "사용자 위치(서울역 부근) + 약국 5곳 (가까운 순/먼 순 섞여 있음)"
        def input = DocumentDto.builder()
                .addressName("서울역")
                .latitude(37.5547d)
                .longitude(126.9707d)
                .build()
        def pharmacies = [
                pharmacy(1L, "P1", 37.5547d, 126.9710d),    // ~30m
                pharmacy(2L, "P2", 37.5560d, 126.9720d),    // ~200m
                pharmacy(3L, "P3", 37.5600d, 126.9800d),    // ~1km
                pharmacy(4L, "P4", 37.6500d, 127.0700d),    // ~14km (필터됨)
                pharmacy(5L, "P5", 37.5650d, 126.9650d),    // ~1.2km
        ]

        when:
        def result = directionService.buildDirectionList(input)

        then:
        1 * pharmacySearchService.searchPharmacyDtoList() >> pharmacies
        result.size() == 3
        result*.targetPharmacyName == ["P1", "P2", "P3"]
        // 거리 오름차순 정렬 검증
        result[0].distance <= result[1].distance
        result[1].distance <= result[2].distance
    }

    def "buildDirectionList: documentDto가 null이면 빈 리스트"() {
        when:
        def result = directionService.buildDirectionList(null)

        then:
        result.isEmpty()
        0 * pharmacySearchService.searchPharmacyDtoList()
    }

    def "saveAll: 빈 리스트 입력 시 repository 호출 없이 빈 리스트"() {
        when:
        def result = directionService.saveAll([])

        then:
        result.isEmpty()
        0 * directionRepository.saveAll(_)
    }

    def "saveAll: 비어있지 않으면 repository에 위임"() {
        given:
        def list = [Direction.builder().targetPharmacyName("X").build()]

        when:
        def result = directionService.saveAll(list)

        then:
        1 * directionRepository.saveAll(list) >> list
        result == list
    }

    def "buildDirectionListByCategoryApi: 카테고리 검색 결과 최대 3개로 제한"() {
        given:
        def input = DocumentDto.builder()
                .addressName("서울역").latitude(37.5547d).longitude(126.9707d).build()
        def categoryResp = new KakaoApiResponseDto()
        categoryResp.documentList = (1..5).collect {
            DocumentDto.builder()
                    .placeName("PH" + it)
                    .addressName("addr" + it)
                    .latitude(37.5d + it * 0.001d)
                    .longitude(127.0d + it * 0.001d)
                    .distance(100.0d * it) // meters
                    .build()
        }

        when:
        def result = directionService.buildDirectionListByCategoryApi(input)

        then:
        1 * kakaoCategorySearchService.requestPharmacyCategorySearch(_, _, _) >> categoryResp
        result.size() == 3
        result*.targetPharmacyName == ["PH1", "PH2", "PH3"]
        result[0].distance == 0.1d // 100m → 0.1km
    }

    def "buildDirectionListByCategoryApi: input이 null이면 빈 리스트"() {
        when:
        def result = directionService.buildDirectionListByCategoryApi(null)

        then:
        result.isEmpty()
        0 * kakaoCategorySearchService.requestPharmacyCategorySearch(_, _, _)
    }

    def "findDirectionUrlById: encoded id를 디코드 후 카카오맵 URL 생성"() {
        given:
        def direction = Direction.builder()
                .id(7L)
                .targetPharmacyName("강남365약국")
                .targetLatitude(37.5d)
                .targetLongitude(127.04d)
                .build()

        when:
        def result = directionService.findDirectionUrlById("encoded")

        then:
        1 * base62Service.decodeDirectionId("encoded") >> 7L
        1 * directionRepository.findById(7L) >> Optional.of(direction)
        result.startsWith("https://map.kakao.com/link/map/")
        result.contains("37.5")
        result.contains("127.04")
    }

    private static PharmacyDto pharmacy(Long id, String name, double lat, double lon) {
        return PharmacyDto.builder()
                .id(id)
                .pharmacyName(name)
                .pharmacyAddress("addr")
                .latitude(lat)
                .longitude(lon)
                .build()
    }
}
