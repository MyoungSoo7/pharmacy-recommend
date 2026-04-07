package com.lms.pharmacyrecommend.api.service

import spock.lang.Specification
import spock.lang.Subject

class KakaoUriBuilderServiceTest extends Specification {

    @Subject
    KakaoUriBuilderService service = new KakaoUriBuilderService()

    def "주소 검색 URI는 query 파라미터를 포함한다"() {
        when:
        def uri = service.buildUriByAddressSearch("서울 강남구")

        then:
        uri.toString().startsWith("https://dapi.kakao.com/v2/local/search/address.json")
        uri.toString().contains("query=")
    }

    def "주소 검색 URI는 한글을 URL 인코딩한다"() {
        when:
        def uri = service.buildUriByAddressSearch("서울")

        then:
        // 인코딩된 결과에는 raw 한글이 들어있지 않아야 함
        !uri.toString().contains("서울")
    }

    def "카테고리 검색 URI는 좌표/반경/카테고리 파라미터를 포함한다"() {
        when:
        def uri = service.buildUriByCategorySearch(37.5, 127.0, 10.0, "PM9")
        def s = uri.toString()

        then:
        s.startsWith("https://dapi.kakao.com/v2/local/search/category.json")
        s.contains("category_group_code=PM9")
        s.contains("x=127.0")
        s.contains("y=37.5")
        s.contains("radius=10000.0")
        s.contains("sort=distance")
    }
}
