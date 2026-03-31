package com.lms.pharmacyrecommend.pharmacy.service

import com.lms.pharmacyrecommend.api.dto.DocumentDto
import com.lms.pharmacyrecommend.api.dto.KakaoApiResponseDto
import com.lms.pharmacyrecommend.api.service.KakaoAddressSearchService
import com.lms.pharmacyrecommend.direction.entity.Direction
import com.lms.pharmacyrecommend.direction.service.Base62Service
import com.lms.pharmacyrecommend.direction.service.DirectionService
import spock.lang.Specification

class PharmacyRecommendationServiceTest extends Specification {

    KakaoAddressSearchService kakaoAddressSearchService = Mock()
    DirectionService directionService = Mock()
    Base62Service base62Service = Mock()

    PharmacyRecommendationService service

    def setup() {
        service = new PharmacyRecommendationService(kakaoAddressSearchService, directionService, base62Service)
    }

    def "주소 검색 결과 없으면 빈 리스트"() {
        when:
        def result = service.recommendPharmacyList("잘못된주소")

        then:
        1 * kakaoAddressSearchService.requestAddressSearch("잘못된주소") >> null
        result.isEmpty()
    }

    def "카카오 API 응답이 빈 경우"() {
        given:
        def response = new KakaoApiResponseDto()
        response.documentList = []

        when:
        def result = service.recommendPharmacyList("서울")

        then:
        1 * kakaoAddressSearchService.requestAddressSearch("서울") >> response
        result.isEmpty()
    }
}
