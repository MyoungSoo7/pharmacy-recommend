package com.lms.pharmacyrecommend.direction.controller

import com.lms.pharmacyrecommend.direction.service.DirectionService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [DirectionController])
class DirectionControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    DirectionService directionService = Mock()

    def "GET /dir/{encodedId} → 카카오맵 URL 리다이렉트"() {
        given:
        def url = "https://map.kakao.com/link/map/P1,37.5,127.0"

        when:
        def result = mockMvc.perform(get("/dir/AAA"))

        then:
        1 * directionService.findDirectionUrlById("AAA") >> url
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url))
    }
}
