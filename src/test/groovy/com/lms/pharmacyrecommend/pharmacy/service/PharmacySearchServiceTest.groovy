package com.lms.pharmacyrecommend.pharmacy.service

import com.lms.pharmacyrecommend.pharmacy.cache.PharmacyRedisTemplateService
import com.lms.pharmacyrecommend.pharmacy.dto.PharmacyDto
import com.lms.pharmacyrecommend.pharmacy.entity.Pharmacy
import spock.lang.Specification

class PharmacySearchServiceTest extends Specification {

    PharmacyRepositoryService pharmacyRepositoryService = Mock()
    PharmacyRedisTemplateService pharmacyRedisTemplateService = Mock()

    PharmacySearchService pharmacySearchService = new PharmacySearchService(
            pharmacyRepositoryService, pharmacyRedisTemplateService
    )

    def "캐시 히트 시 Redis에서 반환"() {
        given:
        def cached = [PharmacyDto.builder().id(1L).pharmacyName("테스트약국").build()]

        when:
        def result = pharmacySearchService.searchPharmacyDtoList()

        then:
        1 * pharmacyRedisTemplateService.findAll() >> cached
        0 * pharmacyRepositoryService.findAll()
        result.size() == 1
        result[0].pharmacyName == "테스트약국"
    }

    def "캐시 미스 시 DB에서 조회 후 Redis에 자동 적재"() {
        given:
        def pharmacy = Pharmacy.builder().id(1L).pharmacyName("DB약국")
                .pharmacyAddress("서울").latitude(37.5).longitude(127.0).build()

        when:
        def result = pharmacySearchService.searchPharmacyDtoList()

        then:
        1 * pharmacyRedisTemplateService.findAll() >> []
        1 * pharmacyRepositoryService.findAll() >> [pharmacy]
        1 * pharmacyRedisTemplateService.save(_)
        result.size() == 1
    }

    def "캐시와 DB 모두 빈 경우"() {
        when:
        def result = pharmacySearchService.searchPharmacyDtoList()

        then:
        1 * pharmacyRedisTemplateService.findAll() >> []
        1 * pharmacyRepositoryService.findAll() >> []
        result.isEmpty()
    }
}
