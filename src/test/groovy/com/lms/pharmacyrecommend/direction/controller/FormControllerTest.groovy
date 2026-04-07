package com.lms.pharmacyrecommend.direction.controller

import com.lms.pharmacyrecommend.direction.dto.OutputDto
import com.lms.pharmacyrecommend.pharmacy.service.PharmacyRecommendationService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(controllers = [FormController])
class FormControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    PharmacyRecommendationService pharmacyRecommendationService = Mock()

    def "GET / 메인 페이지 → main 뷰 200"() {
        expect:
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
    }

    def "POST /search 추천 결과 → output 뷰 + outputFormList 모델"() {
        given:
        def out = OutputDto.builder().pharmacyName("P1").pharmacyAddress("addr")
                .directionUrl("http://x/dir/AAA").roadViewUrl("rv").distance("0.50 km").build()

        when:
        def result = mockMvc.perform(post("/search").param("address", "서울역"))

        then:
        1 * pharmacyRecommendationService.recommendPharmacyList("서울역") >> [out]
        result.andExpect(status().isOk())
                .andExpect(view().name("output"))
                .andExpect(model().attributeExists("outputFormList"))
    }
}
