package com.lms.pharmacyrecommend.pharmacy.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.lms.pharmacyrecommend.pharmacy.dto.PharmacyDto
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class PharmacyRedisTemplateServiceTest extends Specification {

    RedisTemplate<String, Object> redisTemplate = Mock()
    HashOperations<String, String, String> hashOperations = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    PharmacyRedisTemplateService service

    def setup() {
        redisTemplate.opsForHash() >> hashOperations
        service = new PharmacyRedisTemplateService(redisTemplate, objectMapper)
        service.init()
    }

    def "save: 정상 PharmacyDto는 hash에 put되고 TTL이 설정된다"() {
        given:
        def dto = PharmacyDto.builder().id(1L).pharmacyName("X")
                .pharmacyAddress("addr").latitude(37.5d).longitude(127.0d).build()

        when:
        service.save(dto)

        then:
        1 * hashOperations.put("PHARMACY", "1", _ as String)
        1 * redisTemplate.expire("PHARMACY", 24L, TimeUnit.HOURS)
    }

    def "save: dto가 null이면 아무 작업도 하지 않는다"() {
        when:
        service.save(null)

        then:
        0 * hashOperations.put(_, _, _)
        0 * redisTemplate.expire(_, _, _)
    }

    def "save: id가 null이면 아무 작업도 하지 않는다"() {
        given:
        def dto = PharmacyDto.builder().pharmacyName("X").build()

        when:
        service.save(dto)

        then:
        0 * hashOperations.put(_, _, _)
    }

    def "findAll: 캐시 항목을 역직렬화하여 PharmacyDto 리스트로 반환"() {
        given:
        def dto = PharmacyDto.builder().id(1L).pharmacyName("X")
                .pharmacyAddress("addr").latitude(37.5d).longitude(127.0d).build()
        def json = objectMapper.writeValueAsString(dto)

        when:
        def result = service.findAll()

        then:
        1 * hashOperations.entries("PHARMACY") >> ["1": json]
        result.size() == 1
        result[0].id == 1L
        result[0].pharmacyName == "X"
    }

    def "findAll: 예외 발생 시 빈 리스트 반환"() {
        when:
        def result = service.findAll()

        then:
        1 * hashOperations.entries("PHARMACY") >> { throw new RuntimeException("redis down") }
        result.isEmpty()
    }

    def "delete: hash에서 해당 id를 삭제"() {
        when:
        service.delete(42L)

        then:
        1 * hashOperations.delete("PHARMACY", "42")
    }
}
