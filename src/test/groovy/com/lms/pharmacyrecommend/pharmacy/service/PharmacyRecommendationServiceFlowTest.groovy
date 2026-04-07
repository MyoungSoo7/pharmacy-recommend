package com.lms.pharmacyrecommend.pharmacy.service

import com.lms.pharmacyrecommend.api.dto.DocumentDto
import com.lms.pharmacyrecommend.api.dto.KakaoApiResponseDto
import com.lms.pharmacyrecommend.api.service.KakaoAddressSearchService
import com.lms.pharmacyrecommend.direction.entity.Direction
import com.lms.pharmacyrecommend.direction.service.Base62Service
import com.lms.pharmacyrecommend.direction.service.DirectionService
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

class PharmacyRecommendationServiceFlowTest extends Specification {

    KakaoAddressSearchService kakaoAddressSearchService = Mock()
    DirectionService directionService = Mock()
    Base62Service base62Service = Mock()

    PharmacyRecommendationService service

    def setup() {
        service = new PharmacyRecommendationService(kakaoAddressSearchService, directionService, base62Service)
        ReflectionTestUtils.setField(service, "baseUrl", "http://test.local/dir/")
    }

    def "정상 경로: 검색 → 빌드 → 저장 → OutputDto 변환"() {
        given:
        def doc = DocumentDto.builder()
                .addressName("서울역").latitude(37.5547d).longitude(126.9707d).build()
        def resp = new KakaoApiResponseDto()
        resp.documentList = [doc]

        def saved = [
                Direction.builder().id(10L).targetPharmacyName("P1")
                        .targetAddress("addr1").targetLatitude(37.5d).targetLongitude(127.0d)
                        .distance(0.5d).build(),
                Direction.builder().id(11L).targetPharmacyName("P2")
                        .targetAddress("addr2").targetLatitude(37.51d).targetLongitude(127.01d)
                        .distance(1.234d).build()
        ]

        when:
        def result = service.recommendPharmacyList("서울역")

        then:
        1 * kakaoAddressSearchService.requestAddressSearch("서울역") >> resp
        1 * directionService.buildDirectionList(doc) >> saved
        1 * directionService.saveAll(saved) >> saved
        1 * base62Service.encodeDirectionId(10L) >> "AAA"
        1 * base62Service.encodeDirectionId(11L) >> "BBB"

        result.size() == 2
        result[0].pharmacyName == "P1"
        result[0].directionUrl == "http://test.local/dir/AAA"
        result[0].roadViewUrl == "https://map.kakao.com/link/roadview/37.5,127.0"
        result[0].distance == "0.50 km"
        result[1].directionUrl == "http://test.local/dir/BBB"
        result[1].distance == "1.23 km"
    }
}
